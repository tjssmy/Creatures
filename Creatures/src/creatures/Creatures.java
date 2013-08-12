package creatures;

import processing.core.PApplet;
import processing.core.PFont;

public class Creatures extends PApplet {
	//  AgentP:  A Processing-based version of Agent
	// (c) Francis Fukuyama
	//  all of the display-related functions are here

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	static final int DISP_X = 920;  //pixel dimensions of the window
	static final int DISP_Y = 760;
	static final int POND_X = 700;   //pixel dimensions of the pond
	static final int POND_Y = 700;
	static final int BACK_COLOR = 195;
	static final int GRAPH_BUFFER = 210;
	static final int FRAME_RATE = 5;
	PFont ft; 

	String output = "";
	int[] foodGraph = new int[GRAPH_BUFFER];
	int[] creatureGraph = new int[GRAPH_BUFFER];

	int timeClock = 0;
	
	Agent_core core = new Agent_core(POND_X, POND_Y, 5);

	public void setup()  {
		size(DISP_X, DISP_Y);
		background(BACK_COLOR);
		smooth();
		frameRate(FRAME_RATE);
		ft = createFont("Arial", 16, true);
		textFont(ft, 16);
		clearPond(); 
		for (int i=0; i< Agent_core.MAX_CREATURES; i++)  {
			core.agent[i] = new Agent_creature();
			core.agent[i].speed = 1;
		}
		core.init_energy();
		for (int i=0; i<Agent_core.MAX_FOOD; i++)  {
			core.food[i] = new Agent_food();
		}
		core.set_intelligence();
		core.position_creatures(core.agent);
		core.position_food(core.food);
		core.init_energy();
		core.init_buffer();
		init_graphs();
		core.num_creatures = core.count_creatures();
		println("No. of creatures: " + core.num_creatures);
		output = "No. of creatures: " + String.valueOf(core.num_creatures);
		core.total_food = core.count_food();
		String r = "Starting food: " + String.valueOf(core.total_food);
		println(r);
		output = r;
	}

	public void draw()  {
		//  int cr_found = -1;
		//  int seed;
		//  int dist = 1;
		//  int loss = 0;
		//  char dir = 'x';

		timeClock++;  
		clearPond();
		boxScore();
		int plop = core.get_random(Agent_core.GROW_CYCLE);
		if (plop == (Agent_core.GROW_CYCLE - 1))  {
			core.new_food(random(core.WORLD_X),random(core.WORLD_Y));
		}
		core.move_creatures(timeClock);
		drawCreatures();
		drawFood();
		create_creature();
	}

	public void pushCreatures(int cr)  {
		for (int i = (GRAPH_BUFFER - 1); i > 0; i--)  {
			creatureGraph[i] = creatureGraph[i-1];
			creatureGraph[0] = cr;
		}
	}

	public void pushFood(int fd)  {
		for (int i = (GRAPH_BUFFER - 1); i > 0; i--)  {
			foodGraph[i] = foodGraph[i-1];
			foodGraph[0] = fd;
		}
	}

	void boxScore()  {   //draws the panel on right with data
		textFont(ft, 16);
		fill(0);
		text("Creatures: An Agent Model", 710, 15);
		String time = String.valueOf(timeClock);
		text("Time: ", 5, 715);
		text(time, 50, 716);
		core.num_creatures = core.count_creatures();
		pushCreatures(core.num_creatures);
		String l1 = "No. of Creatures: ";
		float w = textWidth(l1);
		text(l1, 710, 620);
		String creatureNum = String.valueOf(core.num_creatures);
		text(creatureNum, (710 + w + 10), 620);
		for (int i = (GRAPH_BUFFER - 1); i >= 0; i--)  {
			strokeWeight(1);
			line((705 + GRAPH_BUFFER - i - 1), 600, (705 + GRAPH_BUFFER - i - 1), 600 - (creatureGraph[i] * 2));
		}
		String l2 = "Total food: ";
		w = textWidth(l2);
		int fd = core.count_food();
		String foodTotal = String.valueOf(fd);
		pushFood(fd);
		text(l2, 710, 185);
		text(foodTotal, (710 + w + 10), 185);
		for (int i = (GRAPH_BUFFER - 1); i >= 0; i--)  {
			strokeWeight(1);
			line((705 + GRAPH_BUFFER - i - 1), 170, (705 + GRAPH_BUFFER - i - 1), 170 - (foodGraph[i] / 15));
		}

		text(output, 250, 716);
		String l3 = "Ave intelligence: ";
		text(l3, 710, 640);
		text(String.valueOf(core.average_intelligence()), 710 + textWidth(l3) + 10, 640);
	}

	void create_creature()  {
		int mouserX, mouserY;
		mouserX = mouseX;
		mouserY = mouseY;
		if ((mouserX >= 0) && (mouserX < POND_X) && (mouserY >= 0) && (mouserY < POND_Y))  {
			if (mousePressed == true) {
				int next = core.find_next_living_creature();
				if (next != -1)  { 
					core.agent[next].alive = true;
					core.agent[next].intelligence = 1;
					core.agent[next].energy = 100;
					//      agent[next].direction =  's';
					core.agent[next].female = core.coin_toss();
					core.agent[next].age = 1;
					core.agent[next].wherex = mouserX / core.SCALEFACTOR;
					core.agent[next].wherey = mouserY / core.SCALEFACTOR;
					core.find_open(next);
					core.num_creatures++;
					println("Spawning creature " + next);
					output = "Spawning creature ";
				}
			}
		}
	}

	public void init_graphs()  {
		for (int i = 0; i < GRAPH_BUFFER; i++)  {
			foodGraph[i] = 0;
			creatureGraph[i] = 0;
		}
	}

	void clearPond()  {
		rectMode(CORNER);
		fill(BACK_COLOR);
		noStroke();
		rect(0,0, DISP_X, DISP_Y);
		fill(255);
		stroke(0);
		strokeWeight(2);
		//  noFill();
		rect(0, 0, POND_X, POND_Y);  //draw pond
	}

	void drawCreatures() {
		ellipseMode(CENTER);
		stroke(0);
		for (int i=0; i<Agent_core.MAX_CREATURES; i++)  {
			if (core.agent[i].alive == true)  {
				float j = (float)core.agent[i].wherex*core.SCALEFACTOR;
				float k = (float)core.agent[i].wherey*core.SCALEFACTOR;
				int sz = core.agent[i].energy / 50;
				if (core.agent[i].female == true)  {
					stroke(255, 0, 0);
				}
				else if (core.agent[i].female == false) {
					stroke(0, 0, 255);
				}
				strokeWeight(3);
				//          int wiggle = timeClock % 2;
				//          if (wiggle == 0)  {
				//            ellipse(j, k, sz, sz/2);
				//          }
				//          else {
				//            ellipse(j, k, sz*2, sz/2);
				//          }
				char d = core.agent[i].direction;
				if (d == 'e')  {
					line(j, k, (j + sz), k);
				}
				else if (d == 'w')  {
					line(j, k, (j - sz), k);
				}
				else if (d == 'n')  {
					line(j, k, j, (k + sz));
				}
				else if (d == 's')  {
					line(j, k, j, (k - sz));
				}
				else if (d == '4') {
					line(j, k, (j + sz), (k + sz));
				}
				else if (d == '3') {
					line(j, k, (j + sz), (k - sz));
				}
				else if (d == '2') {
					line(j, k, (j - sz), (k - sz));
				}
				else if (d == '1') {
					line(j, k, (j - sz), (k + sz));
				}

			}
		}
	}

	void drawFood() {
		rectMode(CENTER);
		noStroke();
		for (int i=0; i<Agent_core.MAX_FOOD; i++)  {
			if (core.food[i].stockpile > 0)  {
				float m = (float)core.food[i].wherex*core.SCALEFACTOR;
				float n = (float)core.food[i].wherey*core.SCALEFACTOR;
				if ((core.food[i].stockpile <=20) && (core.food[i].stockpile > 10))  {
					fill(40);
					rect(m, n, 4, 4);
				}
				else if ((core.food[i].stockpile <=10) && (core.food[i].stockpile > 5))  {
					fill(80);
					rect(m, n, 3, 3);
				}
				else if ((core.food[i].stockpile <=5) && (core.food[i].stockpile > 0))  {
					fill(100);
					rect(m, n, 2, 2);
				}
				else  {
					fill(0);
					rect(m, n, 5, 5);
				}
			}
		}

	}

}
