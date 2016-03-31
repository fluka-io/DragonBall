package dragonball.view.custom;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;

import javax.swing.JPanel;

@SuppressWarnings("serial")
public class JImagePanel extends JPanel {
	private Image image;

	public JImagePanel() {

	}

	public JImagePanel(Image image) {
		setImage(image);
	}

	public void setImage(String filename) {
		setImage(Toolkit.getDefaultToolkit().createImage(filename));
	}

	public void setImage(Image image) {
		this.image = image;
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);

		if (image != null) {
			g.drawImage(image, 0, 0, this);
		}
	}
}
