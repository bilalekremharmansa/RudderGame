package com.bilalekrem.ruddergame.fx.controller;

import com.bilalekrem.ruddergame.util.*;
import com.bilalekrem.ruddergame.game.*;
import com.bilalekrem.ruddergame.game.Game.*;

import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.input.Dragboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.TransferMode;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

public class RudderGameController {
    private RudderGame game;

    private Map<String, Location> locations; // mapping locationString -> Location
    private Map<String, Circle> circles; // mapping locationString -> Circle
    private List<Player> players;

    private static final Paint COLOR_FILL_PIECE_LIGHT = Color.LIGHTSALMON;
    private static final Paint COLOR_FILL_PIECE_DARK = Color.LIGHTSKYBLUE;
    private static final Paint COLOR_FILL_PIECE_DEFAULT = Color.WHITE;
    private static final Paint COLOR_FILL_PIECE_AVALIABLE = Color.LIMEGREEN;
    private static final Paint COLOR_FILL_PIECE_NOT_AVALIABLE = Color.CRIMSON;
    private static final Paint COLOR_STROKE_PIECE_DEFAULT = Color.BLACK;
    private static final double WIDTH = 750, HEIGHT = 750;
    private static final double RADIUS_OF_CIRCLE = 20;
    private static final double COEFFICIENT_STRAIGHT = 75;
    private static final double COEFFICIENT_IN_ANGLES = COEFFICIENT_STRAIGHT / Math.sqrt(2);
    //private static final double COEFFICIENT_IN_ANGLES = 53.03;
    private static final int TOTAL_NUMBER_OF_PIECES = 33;

    public RudderGameController() {
        game = new RudderGame();
        locations = new HashMap<>(TOTAL_NUMBER_OF_PIECES);
        circles = new HashMap<>(TOTAL_NUMBER_OF_PIECES);
        players = new ArrayList<>(2);

        game.initiliazeBoard();
    }           

    public void registerPlayer(String username) {
        Player player = new Player(players.size(), username);
        players.add(player);
    }

    public Scene loadScene() {
        if(players.size() < 2) return null;

        Player[] pls = players.stream().toArray(Player[]::new);
        game.initiliazeGame(pls);        

        AnchorPane root = new AnchorPane();
        Scene scene = new Scene(root, WIDTH, HEIGHT);

        final double X = WIDTH/2;
        final double Y = HEIGHT/2;

        for (int i = 1; i <= RudderGame.LEVEL; i++) {
            Circle circle = new Circle(X, Y, (i*COEFFICIENT_STRAIGHT));
            circle.setFill(Color.TRANSPARENT);
            circle.setStroke(Color.BLACK);
            root.getChildren().add(circle);
        }
        
        Paint fill;

        Location locCenter = new RudderGameLocation(Segment.CENTER, 0);
        Circle circleCenter = createCircle(X, Y, RADIUS_OF_CIRCLE, 0, 0, 
                            COLOR_FILL_PIECE_DEFAULT, COLOR_STROKE_PIECE_DEFAULT, locCenter);
        root.getChildren().add(circleCenter);

        // Segment A-B-C-D
        Player playerOne = pls[0];
        fill = playerOne.pieceType == PieceType.LIGHT ? COLOR_FILL_PIECE_LIGHT : COLOR_FILL_PIECE_DARK;
        for (int i = 0; i < RudderGame.LEVEL ; i++) {
            double straight = (i+1)*COEFFICIENT_STRAIGHT;
            double angle = (i+1)*COEFFICIENT_IN_ANGLES;

            /** 
             * I'm sure that pieces are in order in players.pieces like A1-A2-..B-1-B2..C1....
             * And I know that pieces are in a sequence list. 
             * A1 places at index 0, A2 places at index 1 and so on
             * B1 places at index 4, B2 places at index 5 and so on.
             * As you understand if I increment index LEVEL times for each Segment, I can reach 
             * locations at same level.
             */
            Location locA = playerOne.pieces.get(i).getLocation();
            Location locB = playerOne.pieces.get(i+RudderGame.LEVEL).getLocation();
            Location locC = playerOne.pieces.get(i+RudderGame.LEVEL*2).getLocation();
            Location locD = playerOne.pieces.get(i+RudderGame.LEVEL*3).getLocation();

            // Segment A
            Circle circleA = createCircle(X, Y, RADIUS_OF_CIRCLE, 0, -straight, fill, null, locA);
            // Segment B
            Circle circleB = createCircle(X, Y, RADIUS_OF_CIRCLE, angle, -angle, fill, null, locB);
            // Segment C
            Circle circleC = createCircle(X, Y, RADIUS_OF_CIRCLE, straight, 0, fill, null, locC);
            // Segment D
            Circle circleD = createCircle(X, Y, RADIUS_OF_CIRCLE, angle, angle, fill, null, locD);
                 
            root.getChildren().add(circleA);
            root.getChildren().add(circleB);
            root.getChildren().add(circleC);
            root.getChildren().add(circleD);
        }

        // Segment E-F-G-H
        Player playerTwo = pls[1];
        fill = playerTwo.pieceType == PieceType.LIGHT ? COLOR_FILL_PIECE_LIGHT : COLOR_FILL_PIECE_DARK;
        for (int i = 0; i < RudderGame.LEVEL ; i++) {
            double straight = (i+1)*COEFFICIENT_STRAIGHT;
            double angle = (i+1)*COEFFICIENT_IN_ANGLES;

            // Explanation of i, i+LEVEL .. is above.
            Location locE = playerTwo.pieces.get(i).getLocation();
            Location locF = playerTwo.pieces.get(i+RudderGame.LEVEL).getLocation();
            Location locG = playerTwo.pieces.get(i+RudderGame.LEVEL*2).getLocation();
            Location locH = playerTwo.pieces.get(i+RudderGame.LEVEL*3).getLocation();

            // Segment E
            Circle circleE = createCircle(X, Y, RADIUS_OF_CIRCLE, 0, +straight, fill, null, locE);            
            // Segment F
            Circle circleF = createCircle(X, Y, RADIUS_OF_CIRCLE, -angle, angle, fill, null, locF);     
            // Segment G
            Circle circleG = createCircle(X, Y, RADIUS_OF_CIRCLE, -straight, 0, fill, null, locG);         
            // Segment H
            Circle circleH = createCircle(X, Y, RADIUS_OF_CIRCLE, -angle, -angle, fill, null, locH);

            root.getChildren().add(circleE);
            root.getChildren().add(circleF);
            root.getChildren().add(circleG);
            root.getChildren().add(circleH);
        }

        // mapping locations for drag and dropping.
        locations.put(locCenter.toString(), locCenter); // special case
        circles.put(locCenter.toString(), circleCenter);
        players.forEach(player->{
            player.pieces.stream().forEach( piece -> {
                Location loc = piece.getLocation();
                locations.put(loc.toString(), loc);
            });
        });
        
        return scene;
    }

    private Circle createCircle(double x, double y, double radius, double layoutX,
                                    double layoutY, Paint fill, Paint stroke, Location loc){
        Circle circle = new Circle(x, y, radius);
        circle.setLayoutX(layoutX);
        circle.setLayoutY(layoutY);
        
        if(fill == null) fill = COLOR_FILL_PIECE_DEFAULT;
        if(stroke == null) stroke = COLOR_STROKE_PIECE_DEFAULT;

        circle.setFill(fill);
        circle.setStroke(stroke);

        // It will be used while dragging and dropping to determine source and target location.
        circle.setAccessibleText(loc.toString());

        activateDragAndDrop(circle);

        circles.put(loc.toString(), circle);

        return circle;
    }

    /** Registering listeners to Circle. */
    private void activateDragAndDrop(Circle circle) {
        // src: docs.oracle.com/javafx/2/drag_drop/HelloDragAndDrop.java.html

        // source
        circle.setOnDragDetected(e -> {
            /* allow any transfer mode */
            Dragboard db = circle.startDragAndDrop(TransferMode.ANY);

            /* put a string on dragboard */
            ClipboardContent content = new ClipboardContent();
            content.putString(circle.getAccessibleText());
            db.setContent(content);

            e.consume();
        });

        // target
        circle.setOnDragOver(e -> {
            /* accept it only if it is  not dragged from the same node
                * and if it has a string data */
            if (e.getGestureSource() != circle && e.getDragboard().hasString()) {
                /* allow for both copying and moving, whatever user chooses */
                e.acceptTransferModes(TransferMode.COPY_OR_MOVE);
            }
            e.consume();
        });

        // target
        circle.setOnDragEntered(e -> {
            /* show to the user that it is an actual gesture target */
            if (e.getGestureSource() != circle && e.getDragboard().hasString()) {
                String sourceLoc = ((Circle) e.getGestureSource()).getAccessibleText();
                Location sourceLocation = locations.get(sourceLoc);
                Piece sourcePiece = game.getPiece(sourceLocation);

                players.stream().filter(p -> p.pieces.contains(sourcePiece)).findFirst().ifPresent(player -> {
                    String targetLoc = circle.getAccessibleText();
                    Location targetLocation = locations.get(targetLoc);
    
                    Move move = game.new Move().doer(player.ID).from(sourceLocation).to(targetLocation);

                    MoveType moveType = game.determineMoveType(move);
                    
                    if(moveType == null || moveType == MoveType.NONE) 
                        circle.setFill(COLOR_FILL_PIECE_NOT_AVALIABLE);
                    else
                        circle.setFill(COLOR_FILL_PIECE_AVALIABLE);
                });;
            }

            e.consume();
        });

        // target
        circle.setOnDragExited(e -> {
            /* mouse moved away, remove the graphical cues */
            String locString = circle.getAccessibleText();
            Location loc = locations.get(locString);
            Piece p = game.getPiece(loc);
            circle.setFill(getPaint(p));

            e.consume();
        });

        // target
        circle.setOnDragDropped(e -> {
            /* if there is a string data on dragboard, read it and use it */
            Dragboard db = e.getDragboard();
            boolean success = false;
            if (db.hasString()) {
                Circle sourceCircle = ((Circle) e.getGestureSource());
                String sourceLoc = sourceCircle.getAccessibleText();
                Location sourceLocation = locations.get(sourceLoc);
                Piece sourcePiece = game.getPiece(sourceLocation);

                players.stream().filter(p -> p.pieces.contains(sourcePiece)).findFirst().ifPresent(player -> {

                    String targetLoc = circle.getAccessibleText();
                    Location targetLocation = locations.get(targetLoc);
                    Piece targetPiece = game.getPiece(targetLocation);

                    Move move = game.new Move().doer(player.ID).from(sourceLocation).to(targetLocation);

                    boolean result = game.move(move);

                    if(result) {
                        if(move.type == MoveType.CAPTURE) {
                            Circle capturedCircle = circles.get(move.captured.toString());
                            capturedCircle.setFill(COLOR_FILL_PIECE_DEFAULT);
                        } 
                        sourceCircle.setFill(COLOR_FILL_PIECE_DEFAULT);
                        circle.setFill(getPaint(targetPiece));
                    }
                });
                success = true;
            }
            /* let the source know whether the string was successfully
                * transferred and used */
            e.setDropCompleted(success);

            e.consume();
        });

        // source
        circle.setOnDragDone(e -> {
            /* if the data was successfully moved, clear it */
            if (e.getTransferMode() == TransferMode.MOVE) {
                // do nothing for now
            }

            e.consume();
        });
    }

    private Paint getPaint(Piece piece) {
        if (piece == null) return COLOR_FILL_PIECE_DEFAULT;
        return piece.getType() == PieceType.LIGHT ? COLOR_FILL_PIECE_LIGHT : COLOR_FILL_PIECE_DARK;
    }

}