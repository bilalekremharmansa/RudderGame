package com.bilalekrem.ruddergame.fx.controller;

import com.bilalekrem.ruddergame.util.*;
import com.bilalekrem.ruddergame.game.*;
import com.bilalekrem.ruddergame.game.Game.*;

import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.input.Dragboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.TransferMode;


import java.util.List;
import java.sql.SQLOutput;
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

    @FXML
    ImageView imgFirstPlayer;
    @FXML
    Label nameFirstPlayer;
    @FXML
    Circle circleFirstPlayer;
    @FXML
    ImageView imgSecondPlayer;
    @FXML
    Label nameSecondPlayer;
    @FXML
    Circle circleSecondPlayer;

    @FXML
    Pane gamePane;

    public RudderGameController() {
        System.out.println("const");
        game = new RudderGame();
        locations = new HashMap<>(TOTAL_NUMBER_OF_PIECES);
        circles = new HashMap<>(TOTAL_NUMBER_OF_PIECES);
        players = new ArrayList<>(2);

        game.initiliazeBoard();

        registerPlayer("bilal");
        registerPlayer("ekrem");
    }
    
    @FXML
    private void initialize() {
        System.out.println("init");
        if(players.size() < 2) 
            throw new RuntimeException("Not enough players to play");

        Player[] pls = players.stream().toArray(Player[]::new);
        game.initiliazeGame(pls);
        
        final double X = WIDTH/2;
        final double Y = HEIGHT/2;

        /** Drawing outer circles. These are not Game pieces.*/
        for (int i = 1; i <= RudderGame.LEVEL; i++) {
            Circle circle = new Circle(X, Y, (i*COEFFICIENT_STRAIGHT));
            circle.setFill(Color.TRANSPARENT);
            circle.setStroke(Color.BLACK);
            gamePane.getChildren().add(circle);
        }

        /** mapping locations as locString -> Location for drag and dropping.*/
        game.getLocations().stream().forEach( loc -> {
            locations.put(loc.toString(), loc);
        });

        /** The was not clean, it smelt bad.
         * It does the same thing just before. Creating Circle objects to represents
         * Game locations. 
         */
        Segment[] segments = Segment.values();
        for (Segment segment : segments) {
            if(segment == Segment.CENTER) {
                Circle center = createCircle(X, Y, RADIUS_OF_CIRCLE, Segment.CENTER, 0);
                gamePane.getChildren().add(center);

            } else {
                for (int i = 1; i <= RudderGame.LEVEL; i++) {
                    Circle circle = createCircle(X, Y, RADIUS_OF_CIRCLE, segment, i);
    
                    gamePane.getChildren().add(circle);
                }
            }
        }
    }

    public void registerPlayer(String username) {
        Player player = new Player(players.size(), username);
        players.add(player);
    }


    /**
     * Creates Circle objects by using Segment and level parameters. 
     * After adding maps this objects to circles Map. Also activates dragging 
     * and dropping events.
     * 
     * @param x, coordination of center on x axis
     * @param y, coordination of center on y axis
     * @param radius, radius of center
     * @param segment representis segment of location that assigned to this Circle
     * @param level representslevel of location that assigned to this Circle
     */
    private Circle createCircle(double x, double y, double radius, Segment segment, int level) {
        Circle circle = new Circle(x, y, radius);

        /** Setting layoutX and layoutY */
        double straight = level*COEFFICIENT_STRAIGHT;
        double angle = level*COEFFICIENT_IN_ANGLES;

        double layoutX, layoutY;

        // is the code still smelly ? might be a new function ?
        switch(segment) {
            case A:
                layoutX = 0;
                layoutY = -straight;
                break;
            case B:
                layoutX = angle;
                layoutY = -angle;
                break;
            case C:
                layoutX = straight;
                layoutY = 0;
                break;
            case D:
                layoutX = angle;
                layoutY = angle;
                break;
            case E:
                layoutX = 0;
                layoutY = +straight;
                break;
            case F:
                layoutX = -angle;
                layoutY = angle;
                break;
            case G:
                layoutX = -straight;
                layoutY = 0;
                break;
            case H:
                layoutX = -angle;
                layoutY = -angle;
                break;
            default:
                layoutX = 0;
                layoutY = 0;
                break;
        }

        circle.setLayoutX(layoutX);
        circle.setLayoutY(layoutY);

        String location = Location.parseString(segment, level);
        Piece piece = getPiece(location);
        Paint fill = getFill(piece);
        Paint stroke = COLOR_STROKE_PIECE_DEFAULT;

        circle.setFill(fill);
        circle.setStroke(stroke);

        // It will be used while dragging and dropping to determine source and target location.
        circle.setAccessibleText(location);

        activateDragAndDrop(circle);

        circles.put(location, circle);

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
            circle.setFill(getFill(p));

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
                        circle.setFill(getFill(targetPiece));
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

    /**
     * @param location Piece's location.
     * 
     * @return piece at location.
     */
    private Piece getPiece(String location) {
        Location loc = locations.get(location);

        if(loc == null) return null;

        return game.getPiece(loc);
    }

    /**
     * This method determines color of Piece and returns as Paint object for
     * Circle.setFill
     * 
     * @param piece of color to be determined
     * 
     * @return color of the piece.
     */
    private Paint getFill(Piece piece) {
        if (piece == null) return COLOR_FILL_PIECE_DEFAULT;
        return piece.getType() == PieceType.LIGHT ? COLOR_FILL_PIECE_LIGHT : COLOR_FILL_PIECE_DARK;
    }

}