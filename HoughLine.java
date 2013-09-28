package hello;
import java.awt.Point;
import java.awt.image.BufferedImage; 
 
/** 
 * Represents a linear line as detected by the hough transform. 
 * This line is represented by an angle theta and a radius from the centre. 
 * 
 * @author Olly Oechsle, University of Essex, Date: 13-Mar-2008 
 * @version 1.0 
 */ 
public class HoughLine { 
 
    protected double theta; 
    protected double r; 
 
    /** 
     * Initialises the hough line 
     */ 
    public HoughLine(double theta, double r) { 
        this.theta = theta; 
        this.r = r; 
    } 
 
    /** 
     * Draws the line on the image of your choice with the RGB colour of your choice. 
     */ 
    public double[] getEndpoints(BufferedImage image, int color, int scale) { 

        int height = image.getHeight(); 
        int width = image.getWidth(); 
 
        // During processing h_h is doubled so that -ve r values 
        int houghHeight = (int) (Math.sqrt(2) * Math.max(height, width)) / 2; 
 
        // Find edge points and vote in array 
        float centerX = width / 2; 
        float centerY = height / 2; 
 
        // Draw edges in output array 
        double tsin = Math.sin(theta); 
        double tcos = Math.cos(theta); 
 
        if (theta < Math.PI * 0.25 || theta > Math.PI * 0.75) { 
            // Draw vertical-ish lines 
            int y=0;
            int x = (int) ((((r - houghHeight) - ((y - centerY) * tsin)) / tcos) + centerX); 
            if (x < width && x >= 0) { 
                image.setRGB(x, y, color); 
            } 
            int y2=height;
            int x2 = (int) ((((r - houghHeight) - ((y - centerY) * tsin)) / tcos) + centerX); 
            if (x < width && x >= 0) { 
                image.setRGB(x, y, color); 
            }
            return new double[]{x,y,1};
            
        } else { 
            // Draw horizontal-sh lines 
            int x=0;
            int y = (int) ((((r - houghHeight) - ((x - centerX) * tcos)) / tsin) + centerY); 
            if (y < height && y >= 0) { 
                image.setRGB(x, y, color); 
            }
            int x2=width;
            int y2 = (int) ((((r - houghHeight) - ((x - centerX) * tcos)) / tsin) + centerY); 
            if (y < height && y >= 0) { 
                image.setRGB(x, y, color); 
            }
            return new double[]{x,y,0};
        } 
    } 
} 