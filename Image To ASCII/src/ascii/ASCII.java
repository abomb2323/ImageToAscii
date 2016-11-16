package ascii;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

public final class ASCII {
	boolean negative;
	
	private String asciiShit = "$@B%8&WM#*oahkbdpqwmZO0QLCJUYXzcvunxrjft|1{}[]?-_+~<>i!lI;:,^`. ";
	private String backAscii = " .`^,:;Il!i><~+_-?][}{1|tfjrxnuvczXYUJCLQ0OZmwqpdbkhao*#MW&8%B@$";
	
	private final static double BRIGHTNESS_PERCENTAGE = .8;
	private final static int COMPRESSION_RATIO = 3;
	private final char[] AGS_TABLE = asciiShit.toCharArray();
	private final char[] AGSBACK = backAscii.toCharArray();
	private final int AGS_LENGTH = AGS_TABLE.length;
	private double timeTaken;
	
	public ASCII() {
		this(false);
	}

	public ASCII(final boolean negative) {
		this.negative = negative;
	}

	public String convert(final BufferedImage image) {
		double startTime = System.currentTimeMillis();
		StringBuilder sb = new StringBuilder((image.getWidth() + 1) * image.getHeight());
		
		BufferedImage compImg = new BufferedImage(image.getWidth()/COMPRESSION_RATIO, image.getHeight()/COMPRESSION_RATIO,BufferedImage.TYPE_BYTE_GRAY);
		
		double[][] imgMap = new double[image.getWidth()][image.getHeight()];
		
		for(int i = 0; i < image.getWidth(); i++){
			
			for(int j = 0; j < image.getHeight(); j++){
				Color pixelColor = new Color(image.getRGB(i, j));
				double gValue = ((double) pixelColor.getRed() * 0.2989 + (double) pixelColor.getBlue() * 0.5870 + (double) pixelColor.getGreen() * 0.1140)*(BRIGHTNESS_PERCENTAGE);
				imgMap[i][j] = gValue;
			}
		}
		
		double result = 0;
		
		for(int i = 0; i < image.getWidth()/COMPRESSION_RATIO; i++){
			for(int j = 0; j < image.getHeight()/COMPRESSION_RATIO; j++){
				//Obtain Average Value for a 9x9 square of pixels
				result = 0;
				
				for(int k = i*COMPRESSION_RATIO; k < COMPRESSION_RATIO*(i+1); k++){
					for(int l = j*COMPRESSION_RATIO; l < COMPRESSION_RATIO*(j+1); l++){
						result += imgMap[k][l];
					}
				}
				
				double averageVal = result/Math.pow(COMPRESSION_RATIO,2);
				
				compImg.setRGB(i, j, (int)averageVal);
			}
			
		}
		
		
		
		for (int y = 0; y < compImg.getHeight(); y++) {
			if (sb.length() != 0) sb.append("\n");
			for (int x = 0; x < compImg.getWidth(); x++) {
				Color pixelColor = new Color(compImg.getRGB(x, y));
				double gValue = (double) pixelColor.getRed() * 0.2989 + (double) pixelColor.getBlue() * 0.5870 + (double) pixelColor.getGreen() * 0.1140;
				final char s = negative ? returnStrNeg(gValue) : returnStrPos(gValue);
				sb.append(s);
				sb.append("-");
			}
		}
		
		timeTaken = System.currentTimeMillis()-startTime;
		System.out.println(timeTaken);
		return sb.toString();
	}

	/**
	 * Create a new string and assign to it a string based on the greyscale value.
	 * If the greyscale value is very high, the pixel is very bright and assign characters
	 * such as . and , that do not appear very dark. If the greyscale value is very low the pixel is very dark,
	 * assign characters such as # and @ which appear very dark.
	 *
	 * @param g greyscale
	 * @return char
	 */
	private char returnStrPos(double g)//takes the greyscale value as parameter
	{
		char str = ' ';
		
		//Match greyscale value to the table
		str = AGS_TABLE[(int)g/4];
		return str; // return the character

	}

	/**
	 * Same method as above, except it reverses the darkness of the pixel. A dark pixel is given a light character and vice versa.
	 *
	 * @param g greyscale
	 * @return char
	 */
	private char returnStrNeg(double g) {
		char str = ' ';

		str = AGSBACK[(int)g/4];
		return str;

	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				JFileChooser fileChooser = new JFileChooser();
				fileChooser.setFileFilter(new FileNameExtensionFilter("Images", "jpg", "gif", "png"));
				while (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
					try {
						File f = fileChooser.getSelectedFile();
						final BufferedImage image = ImageIO.read(f);
						if (image == null) throw new IllegalArgumentException(f + " is not a valid image.");
						final String ascii = new ASCII().convert(image);
						final JTextArea textArea = new JTextArea(ascii, image.getHeight()/COMPRESSION_RATIO, image.getWidth()/COMPRESSION_RATIO);
						textArea.setFont(new Font("Monospaced", Font.BOLD, 5));
						textArea.setEditable(true);
						final JDialog dialog = new JOptionPane(new JScrollPane(textArea), JOptionPane.PLAIN_MESSAGE).createDialog(ASCII.class.getName());
						dialog.setResizable(true);
						
						dialog.setVisible(true);
					} catch (Exception e) {
						JOptionPane.showMessageDialog(null, e.toString(), "Error", JOptionPane.ERROR_MESSAGE);
					}
				}
				System.exit(0);
			}
		});
	}

}