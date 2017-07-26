package com.mercury.platform.ui.adr.dialog;


import com.google.gson.Gson;
import com.mercury.platform.shared.config.descriptor.adr.AdrProfileDescriptor;
import com.mercury.platform.ui.adr.components.panel.tree.*;
import com.mercury.platform.ui.adr.components.panel.tree.dialog.AdrDialogTreeNodeRenderer;
import com.mercury.platform.ui.components.fields.font.FontStyle;
import com.mercury.platform.ui.components.panel.VerticalScrollContainer;
import com.mercury.platform.ui.misc.AppThemeColor;
import com.mercury.platform.ui.misc.MercuryStoreUI;
import com.mercury.platform.ui.misc.TooltipConstants;

import javax.swing.*;
import java.awt.*;

public class AdrExportDialog extends AdrDialog<AdrProfileDescriptor> {
    private JTextArea jsonArea;
    private AdrTreePanel adrTree;
    private SwingWorker<AdrTreePanel,Void> worker;
    public AdrExportDialog(Component relative, AdrProfileDescriptor descriptor){
        super(relative, descriptor);
        this.setTitle("Export manager");

        MercuryStoreUI.adrUpdateTree.subscribe(state -> {
            this.repaint();
        });

        this.worker = new SwingWorker<AdrTreePanel, Void>() {
            @Override
            protected AdrTreePanel doInBackground() throws Exception {
                adrTree.updateTree();
                return null;
            }

            @Override
            protected void done() {
                adrTree.setVisible(true);
                pack();
                repaint();
            }
        };
    }

    @Override
    protected void createView() {
        this.setPreferredSize(new Dimension(600,500));
        JPanel root = this.componentsFactory.getJPanel(new BorderLayout());
        root.add(this.getDataPanel(),BorderLayout.CENTER);
        root.add(this.getViewPanel(),BorderLayout.LINE_END);
        this.add(this.componentsFactory.wrapToSlide(root),BorderLayout.CENTER);
    }

    @Override
    protected void postConstruct() {
        this.jsonArea.setText(this.getPayloadAsJson());
        this.adrTree.setVisible(true);
        this.worker.execute();
        this.pack();
        this.repaint();
    }

    private JPanel getDataPanel(){
        JPanel root = this.componentsFactory.getJPanel(new BorderLayout());
        root.setBorder(BorderFactory.createLineBorder(AppThemeColor.ADR_PANEL_BORDER));
        root.setBackground(AppThemeColor.ADR_BG);
        JLabel header = this.componentsFactory.getTextLabel("Data (Ctrl + A for copy):", FontStyle.BOLD,18);
        header.setForeground(AppThemeColor.TEXT_NICKNAME);
        root.add(header,BorderLayout.PAGE_START);

        JPanel container = new VerticalScrollContainer();
        container.setBackground(AppThemeColor.ADR_BG);
        container.setLayout(new BoxLayout(container,BoxLayout.Y_AXIS));
        JScrollPane verticalContainer = this.componentsFactory.getVerticalContainer(container);

        this.jsonArea = this.componentsFactory.getSimpleTextArea("");
        this.jsonArea.setBorder(BorderFactory.createLineBorder(AppThemeColor.ADR_DEFAULT_BORDER));
        this.jsonArea.setMinimumSize(new Dimension(450,550));
        this.jsonArea.setBackground(AppThemeColor.ADR_TEXT_ARE_BG);
        container.add(this.jsonArea,AppThemeColor.ADR_BG);
        root.add(this.componentsFactory.wrapToSlide(verticalContainer,AppThemeColor.ADR_BG,0,5,4,0),BorderLayout.CENTER);
        return this.componentsFactory.wrapToSlide(root);
    }
    private JPanel getViewPanel(){
        JPanel root = this.componentsFactory.getJPanel(new BorderLayout());
        root.setPreferredSize(new Dimension(240,100));
        root.setBorder(BorderFactory.createLineBorder(AppThemeColor.ADR_PANEL_BORDER));
        root.setBackground(AppThemeColor.FRAME_RGB);
        JPanel headerPanel = this.componentsFactory.getJPanel(new BorderLayout());
        headerPanel.setBackground(AppThemeColor.ADR_BG);

        JButton addButton = this.componentsFactory.getIconButton("app/adr/add_node.png", 15, AppThemeColor.FRAME, TooltipConstants.ADR_ADD_BUTTON);
        addButton.setBackground(AppThemeColor.ADR_BG);
        JLabel header = this.componentsFactory.getTextLabel("View:", FontStyle.BOLD,18);
        header.setForeground(AppThemeColor.TEXT_NICKNAME);
        headerPanel.add(header,BorderLayout.CENTER);
        headerPanel.add(addButton,BorderLayout.LINE_END);
        root.add(headerPanel,BorderLayout.PAGE_START);

        this.adrTree = new AdrTreePanel(this.payload.getContents(), new AdrDialogTreeNodeRenderer());
        this.adrTree.setVisible(false);
        root.add(this.componentsFactory.wrapToSlide(this.adrTree,AppThemeColor.FRAME_RGB),BorderLayout.CENTER);
        return this.componentsFactory.wrapToSlide(root);
    }
    private String getPayloadAsJson(){
        return new Gson().toJson(this.payload);
    }
}