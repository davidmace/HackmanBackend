package hello;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Vector;
import javax.swing.JApplet;

public class HackMan extends JApplet{

	BufferedImage frame=null;
	static int scale=4;
	
	public void init() {
		
		//get image
		String filename = "page.png"; 
		try {
			frame = javax.imageio.ImageIO.read(new File(filename));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//BufferedImage scaled = scaleDown(frame);
		BufferedImage canny = edgeDetect(frame);
		frame=canny;
		Vector<HoughLine> lines = getEdges(canny);
		ArrayList<Line> segments = getSegments(lines, canny, frame);
		cleanSegments(segments);
		char[][] map = makeMap(segments, frame);
		
		for(int i=0; i<map.length; i++) {
			for(int j=0; j<map[0].length; j++) {
				System.out.print(map[i][j]);
			}
			System.out.println();
		}
		
        
        for(int i=0; i<segments.size(); i++) {
        	Line segment = segments.get(i);
        	if(segment.sy==segment.ey) {
        		for(int x=(int)segment.sx; x<segment.ex; x++)
        			frame.setRGB(x, (int)segment.sy, new Color(255,0,0).getRGB());
        	}
        	else {
        		for(int y=(int)segment.sy; y<segment.ey; y++)
        			frame.setRGB((int)segment.sx, y, new Color(255,0,0).getRGB());
        	}
        }
        
	}
	
	private static BufferedImage scaleDown(BufferedImage frame) {
		//scale the image down
		Image imgNoBuff = frame.getScaledInstance(frame.getWidth()/scale, frame.getHeight()/scale, Image.SCALE_FAST);
        BufferedImage img = new BufferedImage(frame.getWidth()/scale, frame.getHeight()/scale, BufferedImage.TYPE_INT_RGB);
        Graphics g = img.createGraphics();
        g.drawImage(imgNoBuff, 0, 0, new Color(0,0,0), null);
        g.dispose();
        return img;
	}
	
	private static BufferedImage edgeDetect(BufferedImage img){
		CannyEdgeDetector detector = new CannyEdgeDetector();
		detector.setLowThreshold(0.5f);
		detector.setHighThreshold(1f);
		detector.setSourceImage(img);
		detector.process();
		BufferedImage canny = detector.getEdgesImage();
		return canny;
	}
	
	private static Vector<HoughLine> getEdges(BufferedImage canny) {
		HoughTransform h = new HoughTransform(canny.getWidth(), canny.getHeight()); 
        h.addPoints(canny); 
        Vector<HoughLine> lines = h.getLines(30);

        return lines;
	}
	
	private static ArrayList<Line> getSegments(Vector<HoughLine> lines, BufferedImage canny, BufferedImage frame) {
		ArrayList<Line> segments = new ArrayList<Line>();
		ArrayList<Line> hors = new ArrayList<Line>(), verts = new ArrayList<Line>();
		for (int j = 0; j < lines.size(); j++                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                  ) { 
            HoughLine line = lines.elementAt(j); 
            double[] vals = line.getEndpoints(frame, Color.RED.getRGB(), scale); 
            
            if(vals[2]==0) { //horizontal
            	int start=-1, end=-1;
            	ArrayList<Line> localSegments = new ArrayList<Line>();
            	for(int i=0; i<frame.getWidth(); i++) {
            		if(i>=canny.getWidth())
            			break;
            		boolean anywhite=false;
            		for(int dj=-2; dj<=2; dj++) {
            			if((int)vals[1]+dj>=canny.getHeight() || i+dj<0)
                			break;
            			if(canny.getRGB(i, (int)vals[1]+dj)==-1) {
            				anywhite=true; break;
            			}
            		}
            		if(anywhite && start<0) {
            			start=i;
            		}
            		if(!anywhite && start>=0) {
            			end=i;
            			localSegments.add(new Line(start, (int)vals[1], end , (int)vals[1], true));
            			start=-1;
            		}	
            	}
            	for(int i=0; i<localSegments.size()-1; i++) {
            		if(localSegments.get(i+1).sx-localSegments.get(i).ex<10) {
            			localSegments.set(i+1,new Line(localSegments.get(i).sx,localSegments.get(i+1).sy,localSegments.get(i+1).ex,localSegments.get(i+1).sy, true));
            			localSegments.remove(i);
            			i--;
            		}
            	}
            	for(int i=0; i<localSegments.size(); i++) {
            		if(localSegments.get(i).ex-localSegments.get(i).sx<=10) {
            			localSegments.remove(i);
            			i--;
            		}
            	}
        		hors.addAll(localSegments);
            }            
            else { //vertical
            	int start=-1, end=-1;
            	ArrayList<Line> localSegments = new ArrayList<Line>();
            	for(int i=0; i<frame.getHeight(); i++) {
            		if(i>=canny.getHeight())
            			break;
            		boolean anywhite=false;
            		for(int dj=-2; dj<=2; dj++) {
            			if((int)vals[0]+dj>=canny.getWidth() || i+dj<0)
                			break;
            			if(canny.getRGB((int)vals[0]+dj, i)==-1) {
            				anywhite=true; break;
            			}
            		}
            		if(anywhite && start<0) {
            			start=i;
            		}
            		if(!anywhite && start>=0) {
            			end=i;
            			localSegments.add(new Line((int)vals[0], start, (int)vals[0], end, false));
            			start=-1;
            		}
            	}
            	for(int i=0; i<localSegments.size()-1; i++) {
            		if(localSegments.get(i+1).sy-localSegments.get(i).ey<10) {
            			localSegments.set(i+1,new Line(localSegments.get(i).sx,localSegments.get(i).sy,localSegments.get(i).sx,localSegments.get(i+1).ey, false));
            			localSegments.remove(i);
            			i--;
            		}
            	}
            	for(int i=0; i<localSegments.size(); i++) {
            		if(localSegments.get(i).ey-localSegments.get(i).sy<=10) {
            			localSegments.remove(i);
            			i--;
            		}
            	}
        		verts.addAll(localSegments);
            }
        }
		
		for(int i=0; i<hors.size()-1; i++) {
        	if(Math.abs(hors.get(i).sy-hors.get(i+1).sy)<15) {
        		if(hors.get(i).ex-hors.get(i).sx>hors.get(i+1).ex-hors.get(i+1).sx) {
        			hors.remove(i+1);
        			i--;
        		}
        		else {
        			hors.remove(i);
        			i--;
        		}
        	}
        }
		segments.addAll(hors);
		
		for(int i=0; i<verts.size()-1; i++) {
        	if(Math.abs(verts.get(i).sx-verts.get(i+1).sx)<15) {
        		if(verts.get(i).ey-verts.get(i).sy>verts.get(i+1).ey-verts.get(i+1).sy) {
        			verts.remove(i+1);
        			i--;
        		}
        		else {
        			verts.remove(i);
        			i--;
        		}
        	}
        }
		
		segments.addAll(verts);
        return segments;
	}	
	
	//calc closest segment, if it is a segment this crosses then we cut off the end, otherwise we extend the end
	public void cleanSegments(ArrayList<Line> segments) {
		for(int i=0; i<segments.size(); i++) {
			boolean hor = segments.get(i).sy==segments.get(i).ey;
			int bestHS=Integer.MAX_VALUE, bestHE=Integer.MAX_VALUE, bestVS=Integer.MAX_VALUE, bestVE=Integer.MAX_VALUE;
			int valHS=-1, valHE=-1, valVS=-1, valVE=-1;
			for(int j=0; j<segments.size(); j++) {
				boolean hor2 = segments.get(j).sy==segments.get(j).ey;
				if(hor2==hor)
					continue;
				if(hor && !hor2) {
					if(!( segments.get(i).sy<=segments.get(j).ey && segments.get(i).sy>=segments.get(j).sy ))
						continue;
					System.out.println(segments.get(i).sx+" "+segments.get(j).sx);
					int dist = (int)Math.abs( segments.get(i).sx-segments.get(j).sx );
					if(dist<bestHS) {
						bestHS=dist;
						valHS=(int)segments.get(j).sx;
					}
					dist = (int)Math.abs( segments.get(i).ex-segments.get(j).sx );
					if(dist<bestHE) {
						bestHE=dist;
						valHE=(int)segments.get(j).sx;
					}
				}
				else if(!hor && hor2) {
					if(!( segments.get(i).sx<=segments.get(j).ex && segments.get(i).sx>=segments.get(j).sx ))
						continue;
					int dist = (int)Math.abs( segments.get(i).sy-segments.get(j).sy );
					if(dist<bestVS) {
						bestVS=dist;
						valVS=(int)segments.get(j).sy;
					}
					dist = (int)Math.abs( segments.get(i).ey-segments.get(j).sy );
					if(dist<bestVE) {
						bestVE=dist;
						valVE=(int)segments.get(j).sy;
					}
				}
				
			}
			if(valHS>=0) segments.get(i).sx=valHS;
			if(valVS>=0) segments.get(i).sy=valVS;
			if(valHE>=0) segments.get(i).ex=valHE;
			if(valVE>=0) segments.get(i).ey=valVE;	
		}
	}
	
	private static char[][] makeMap(ArrayList<Line> lines, BufferedImage frame) {
		//circle= food
		//space= everything else
		//line at end of row
		//28 wide, 45 long
		int width=28, height=45;
		int posWidth=frame.getWidth()/width;
		int posHeight=frame.getHeight()/height;
		char[][] map=new char[width+1][height];
		for(int x=0; x<width; x++) {
			for(int y=0; y<height; y++) {
				map[x][y]=' ';
			}
		}
		for(int i=0; i<lines.size(); i++) {
			Line line=lines.get(i);
			if(line.hor) {
				for(int x=line.sx/posWidth; x<=line.ex/posWidth; x++) {
					map[x][line.sy/posHeight]='o';
				}
			}
			else {
				for(int y=line.sy/posHeight; y<=line.ey/posHeight; y++) {
					map[line.sx/posWidth][y]='o';
				}
			}
		}
		for(int i=0; i<height; i++) {
			map[width][i]='|';
		}
		return map;
	}
	
	public void paint(Graphics g) {
		g.drawImage(frame, 0, 0, null);
	}
	
}
