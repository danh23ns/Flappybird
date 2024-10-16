import javax.swing.*;
import java.awt.*;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Random;

public class FlappyBird extends JPanel implements ActionListener, KeyListener {
    int boardWidth = 360;
    int boardHeight = 640;

    Image background;
    Image birdimg;
    Image topPipeimg;
    Image botPipeimg;

    //bird
    int birdX = boardWidth/8;
    int birdY = boardHeight/2;
    int birdWidth = 34;
    int birdHeight = 24;


    class Bird{
        int x = birdX;
        int y = birdY;
        int width = birdWidth;
        int height = birdHeight;
        Image img;

        Bird(Image img){
            this.img = img;
        }
    }

    //Pipes
    int pipeX = boardWidth;
    int pipeY = 0;

    int pipeWidth = 64;
    int pipeHeight = 512;

    class Pipe{
        int x = pipeX;
        int y = pipeY;
        int width = pipeWidth;
        int height = pipeHeight;
        Image img;
        boolean passed = false;

        Pipe(Image img){
            this.img=img;
        }
    }

    //game logic
    Bird bird;
    int VelocityX = -4; //dua cot qua ben trai (gia lap chim dang bay)
    int velocityY = 0; //truc oxy co truc y nguoc moi lan nhay la -9
    int gravity = 1; //rang buoc roi

    ArrayList<Pipe> pipes;

    Random random = new Random();

    Timer gameLoop;
    Timer placePipesTimer;

    boolean gameOver = false;

    double score = 0;

    FlappyBird(){
        setPreferredSize(new Dimension(boardWidth, boardHeight));
        //setBackground(Color.blue);
        setFocusable(true);
        addKeyListener(this);

        //load images
        background = new ImageIcon(getClass().getResource("./flappybirdbg.png")).getImage();
        birdimg = new ImageIcon(getClass().getResource("./flappybird.png")).getImage();
        topPipeimg = new ImageIcon(getClass().getResource("./toppipe.png")).getImage();
        botPipeimg = new ImageIcon(getClass().getResource("./bottompipe.png")).getImage();

        //bird
        bird = new Bird(birdimg);
        pipes = new ArrayList<Pipe>();

        //place pipes timer
        placePipesTimer = new Timer(1500, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                placePipes();
            }
        });
        placePipesTimer.start();


        //game timer
        gameLoop = new Timer(1000/60, this); //1000/60 = 16.6
        gameLoop.start();
    }

    public void placePipes(){
        //(0-1)*pipeHeight/2 -> (0-256)
        //128
        //0 -128 - (0-256) --> pipeHeight/4 -> 3/4 pineHeight

        int randomPipeY = (int) (pipeY - pipeHeight/4 - Math.random()*pipeHeight/2);
        int openingSpace = boardHeight/4; // mac dinh khoang cach giua cot tren va duoi nen khong can random cac cot duoi

        Pipe topPipe = new Pipe(topPipeimg);
        topPipe.y = randomPipeY;
        pipes.add(topPipe);

        Pipe bottomPipe = new Pipe(botPipeimg);
        bottomPipe.y = topPipe.y + pipeHeight + openingSpace;
        pipes.add(bottomPipe);
    }

    public void paintComponent(Graphics g){
        super.paintComponent(g);
        draw(g);
    }
    public void draw(Graphics g){

        //background
        g.drawImage(background, 0, 0, boardWidth, boardHeight, null);

        //bird
        g.drawImage(bird.img, bird.x, bird.y, bird.width, bird.height, null);

        for(int i = 0; i < pipes.size(); i++){
            Pipe pipe = pipes.get(i);
            g.drawImage(pipe.img, pipe.x, pipe.y, pipe.width, pipe.height, null);

            g.setColor(Color.white);
            g.setFont(new Font("Arial", Font.PLAIN, 32));
            if(gameOver){
                g.drawString("Game over: " + String.valueOf((int) score), 10, 35);
            }else {
                g.drawString(String.valueOf((int) score), 10, 35);
            }
        }
    }

    public void move(){
        //bird
        velocityY += gravity; // neu ko di chuyen toa do se mac dinh -9, chuong trinh tiep tuc chay toa do se giam trong vong lap (-6+1)
        bird.y += velocityY; // phai luon nhay de toa do va gravity luon co khaong cach (tuc toa do se khong giam ra ngoai man hinh)
        bird.y =Math.max(bird.y, 0); //gioi han khung hinh

        //pipes
        for(int i=0; i<pipes.size(); i++){
            Pipe pipe =pipes.get(i);
            pipe.x += VelocityX;

            if(!pipe.passed && bird.x > pipe.x + pipe.width){
                pipe.passed = true;
                score += 0.5; // qua 2 cot = 1d
            }

            if(collision(bird, pipe)){
                gameOver = true;
            }
        }

        if(bird.y > boardHeight){
            gameOver = true;
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        move();
        repaint();
        if(gameOver){
            placePipesTimer.stop();
            gameLoop.stop();
        }
    }

    public boolean collision(Bird a, Pipe b){
        return a.x < b.x + b.width && a.x + a.width > b.x && a.y < b.y + b.height && a.y + a.height > b.y;
        // cham cot
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if(e.getKeyCode() == KeyEvent.VK_SPACE){
            velocityY=-9;
            if(gameOver){
                //restart
                bird.y = birdY;
                velocityY = 0;
                pipes.clear();
                score=0;
                gameOver=false;
                gameLoop.start();
                placePipesTimer.start();
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }
}
