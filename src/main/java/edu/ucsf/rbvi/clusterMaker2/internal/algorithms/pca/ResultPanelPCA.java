/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.ucsf.rbvi.clusterMaker2.internal.algorithms.pca;

import edu.ucsf.rbvi.clusterMaker2.internal.algorithms.NodeCluster;
import edu.ucsf.rbvi.clusterMaker2.internal.ui.ResultsPanel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.util.HashMap;
import java.util.Map;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.view.model.CyNetworkView;

/**
 *
 * @author root
 */
public class ResultPanelPCA extends JPanel{
    
        private final CyNetwork network;
        private CyNetworkView networkView;
        private final ComputationMatrix[] components;

        // table size parameters
        private static final int graphPicSize = 80;
        private static final int defaultRowHeight = graphPicSize + 8;
        
        private ResultPanelPCA.PCBrowserPanel pcBrowserPanel;

        public ResultPanelPCA(final ComputationMatrix[] components, 
                final CyNetwork network, 
                final CyNetworkView networkView){

                this.network = network;
                this.networkView = networkView;
                this.components = components;
                
                this.pcBrowserPanel = new PCBrowserPanel();
                add(pcBrowserPanel, BorderLayout.CENTER);
		this.setSize(this.getMinimumSize());
        }
    
        /**
	 * Panel that contains the browser table with a scroll bar.
	 */
	private class PCBrowserPanel extends JPanel implements ListSelectionListener {
		private final ResultPanelPCA.PCBrowserTableModel browserModel;
		private final JTable table;

		public PCBrowserPanel() {
			super();

			setLayout(new BorderLayout());

			// Create the summary panel
			String title = "Title Here";
			TitledBorder border = 
				BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), title);
			border.setTitlePosition(TitledBorder.TOP);
			border.setTitleJustification(TitledBorder.LEFT);
			border.setTitleColor(Color.BLUE);

			JLabel summary = new JLabel("<html>"+"here is the summery"+"</html>");
			summary.setBorder(border);
			add(summary, BorderLayout.NORTH);

			// main data table
			browserModel = new ResultPanelPCA.PCBrowserTableModel();

			table = new JTable(browserModel);
			table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
			table.setAutoCreateRowSorter(true);
			table.setDefaultRenderer(StringBuffer.class, new ResultsPanel.JTextAreaRenderer(defaultRowHeight));
			table.setIntercellSpacing(new Dimension(0, 4)); // gives a little vertical room between clusters
			table.setFocusable(false); // removes an outline that appears when the user clicks on the images

			//System.out.println("CBP: after setting table params");

			// Ask to be notified of selection changes.
			ListSelectionModel rowSM = table.getSelectionModel();
			rowSM.addListSelectionListener(this);

			JScrollPane tableScrollPane = new JScrollPane(table);
			//System.out.println("CBP: after creating JScrollPane");
			tableScrollPane.getViewport().setBackground(Color.WHITE);

			add(tableScrollPane, BorderLayout.CENTER);
			//System.out.println("CBP: after adding JScrollPane");

			JButton dispose = new JButton("Close");
			dispose.addActionListener(new AbstractAction() {
				@Override
				public void actionPerformed(ActionEvent e) {
                                    System.out.println("Close clicked");
				}
			});

			JPanel buttonPanel = new JPanel();
			buttonPanel.add(dispose);
			add(buttonPanel, BorderLayout.SOUTH);
		}

		public int getSelectedRow() {
			return table.getSelectedRow();
		}

		public void update(final ImageIcon image, final int row) {
			table.setValueAt(image, row, 0);
		}

		public void update(final NodeCluster cluster, final int row) {
			final String score = "needs score here";
			table.setValueAt(score, row, 1);
		}

		JTable getTable() { 
			return table;
		}

		public void valueChanged(ListSelectionEvent e) {
			ListSelectionModel lsm = (ListSelectionModel) e.getSource();
			// Get the rows
			int[] rowIndices = table.getSelectedRows();
			Map<CyNode, CyNode> selectedMap = new HashMap<CyNode, CyNode>();
			// Get the clusters
			for (int i = 0; i < rowIndices.length; i++) {
                            System.out.println("PC is selected");
			}
			// Select the nodes
			for (CyNode node: network.getNodeList()) {
				if (selectedMap.containsKey(node))
					network.getRow(node).set(CyNetwork.SELECTED, true);
				else
					network.getRow(node).set(CyNetwork.SELECTED, false);
			}

			// I wish we didn't need to do this, but if we don't, the selection
			// doesn't update
			networkView.updateView();
		}
	}
    
        private class PCBrowserTableModel extends AbstractTableModel {

		private final String[] columnNames = { "PC", "Description" };
		private final Object[][] data; // the actual table data

		public PCBrowserTableModel() {
                    
			data = new Object[components.length][columnNames.length];

			for (int i = 0; i < components.length; i++) {
				String details = "values";
				data[i][1] = new StringBuffer(details);
                                
				final Image image = createPCImage();
                                
				data[i][0] = image != null ? new ImageIcon(image) : new ImageIcon();
			}
		}

		@Override
		public String getColumnName(int col) {
			return columnNames[col];
		}

		public int getColumnCount() {
			return columnNames.length;
		}

		public int getRowCount() {
			return data.length;
		}

		public Object getValueAt(int row, int col) {
			return data[row][col];
		}

		@Override
		public void setValueAt(Object object, int row, int col) {
			data[row][col] = object;
			fireTableCellUpdated(row, col);
		}

		@Override
		public Class<?> getColumnClass(int c) {
			return getValueAt(0, c).getClass();
		}
	}
    
        public Image createPCImage(){
            final Image image = null;
            return image;
        }
    
        public static void createAndShowGui(final ComputationMatrix[] components, 
                final CyNetwork network, 
                final CyNetworkView networkView){
            
            ResultPanelPCA resultPanelPCA = new ResultPanelPCA(components, network, networkView);
            
            JFrame frame = new JFrame("Result Panel");
            
            frame.getContentPane().add(resultPanelPCA);
            frame.pack();
            frame.setLocationByPlatform(true);
            frame.setVisible(true);
        }
}
