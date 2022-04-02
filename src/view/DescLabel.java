package view;

import java.awt.Font;

import javax.swing.JLabel;

class DescLabel extends JLabel {

    public DescLabel(String text) {

        super(text);
        this.setFont(new Font("Sans", Font.PLAIN, 14));
        this.setAlignmentX(JLabel.RIGHT_ALIGNMENT);

    }    
}

class HeadLabel extends JLabel {

    public HeadLabel(String text) {

        super(text);
        this.setFont(new Font("Sans", Font.BOLD, 16));
        this.setAlignmentX(JLabel.RIGHT_ALIGNMENT);

    }
}

class HeadDescLabel extends JLabel {

    public HeadDescLabel(String text) {

        super(text);
        // this.setFont(new Font("Sans", Font.BOLD, 18));
        this.setAlignmentX(JLabel.LEFT_ALIGNMENT);

    }
}
