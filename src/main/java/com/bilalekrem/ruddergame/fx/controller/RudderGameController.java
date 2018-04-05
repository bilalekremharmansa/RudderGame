package com.bilalekrem.ruddergame.fx.controller;

import com.bilalekrem.ruddergame.net.ClientObserver;
import com.bilalekrem.ruddergame.net.Message;
import com.bilalekrem.ruddergame.net.Message.MessageType;
import com.bilalekrem.ruddergame.util.*;
import com.bilalekrem.ruddergame.fx.RudderGameApp;
import com.bilalekrem.ruddergame.game.*;
import com.bilalekrem.ruddergame.game.Game.*;
import com.bilalekrem.ruddergame.game.Game.Move.MoveType;

import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.input.Dragboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.TransferMode;

import java.util.Map;
import java.util.HashMap;

public class RudderGameController implements ClientObserver {
    private RudderGameApp app;

    private RudderGame game;
    private Player player; // the player who plays on this session.
    private boolean ONLINE_MODE;

    private Map<String, Location> locations; // mapping locationString -> Location
    private Map<String, Circle> circles; // mapping locationString -> Circle

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
        game = new RudderGame();
        game.initiliazeBoard();
        locations = new HashMap<>(TOTAL_NUMBER_OF_PIECES);
        circles = new HashMap<>(TOTAL_NUMBER_OF_PIECES);
    }

    public void setApplication(RudderGameApp app) {
        this.app = app;
    }

    public void start(String name, boolean online) {
        this.player = new Player(-1, name);
        this.ONLINE_MODE = online;
        if(online) {
            app.clientInstance().registerObserver(this);
            app.clientInstance().start();
        }else {
            Player otherPlayer = new Player(1, "Other");
            Player[] players = {player, otherPlayer};
            initGameBoardUI(players);
        }
        
        
    }
    
    @FXML
    private void initialize() { }

    /** these codes were in initiliaze() method. However, initiliaze method
     * invokes when Controller's loading which we dont want this. This method
     * must be call when server send players data.
     */
    private void initGameBoardUI(Player[] players) {
        // initiliazing rudder game
        game.initiliazeGame(players);

        /** setting user names and piece colors on GUI */
        Player opponent;
        if(players[0].ID == player.ID) {
            player = players[0];
            opponent = players[1];
        } else {
            player = players[1]
            opponent = players[0];
        }

        nameFirstPlayer.setText(player.name);
        nameSecondPlayer.setText(opponent.name);

        circleFirstPlayer.setFill(getFill(player.pieces.get(0)));
        circleSecondPlayer.setFill(getFill(opponent.pieces.get(0)));

        final double X = WIDTH/2;
        final double Y = HEIGHT/2;
        final Segment[] segments = Segment.values();

        /** Drawing outer circles. These are not Game pieces.*/
        for (int i = 1; i <= RudderGame.LEVEL; i++) {
            Circle circle = new Circle(X, Y, (i*COEFFICIENT_STRAIGHT));
            circle.setFill(Color.TRANSPARENT);
            circle.setStroke(Color.BLACK);
            gamePane.getChildren().add(circle);
        }
        /** Drawing lines from center to segment's last level. */
        for (Segment segment : segments) {
            if(segment == Segment.CENTER) continue;

            double[] layouts = calculateLayout(segment, RudderGame.LEVEL);
            Line line = new Line(X, Y, X + layouts[0], Y + layouts[1]);
            gamePane.getChildren().add(line);
        }

        /** mapping locations as locString -> Location for drag and dropping.*/
        game.getLocations().stream().forEach( loc -> {
            locations.put(loc.toString(), loc);
        });

        /** The code was not clean, it smelt bad.
         * It does the same thing just before. Creating Circle objects to represents
         * Game locations. 
         */
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

    private void gameOver() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Game is over!");
        alert.setHeaderText("It's over.");
        alert.setContentText("This is done.");
        alert.showAndWait();   
    }

    @Override
	public void update(Message message) {
        System.out.println("Message geldi");
        System.out.println(message);

        switch(message.type()) {
            case GREETING:
                Player serverPlayer = message.content(Player.class);
                this.player.ID = serverPlayer.ID;
                
                /** send player's name to server. */
                Message greeting = new Message(player.ID, MessageType.GREETING, player);
                app.clientInstance().send(greeting);
                break;
            case PLAYERS:
                Player[] players = message.content(Player[].class);
                runOnGUI(() -> initGameBoardUI(players));
                break;
            case MOVE:
                Move move = message.content(Move.class);
                boolean result = game.move(move);
                if(result ){
                    runOnGUI(() -> move(move));
                }
                break;
            default:
                break;
        }
    }
    
    /**
     * initGameBoard updates fx ui. This update method works on
     * Client Thread. Client Thread is not allowed to modify 
     * ui, for this purpose the code works below.
     * 
     * "Platform.runLater: If you need to update a GUI component 
     * from a non-GUI thread, you can use that to put your 
     * update in a queue and it will be handle by the GUI thread 
     * as soon as possible."
     * 
     * stackoverflow.com/a/13785931/5929406
     */
    public void runOnGUI(Runnable runnable) {
        Platform.runLater(runnable);
    }

    /**
     * This method invokes to notify a player with a new move. However, it's type property
     * needs to be assigned a value. If type == null, then there will be no updating UI. At
     * least it will be a MOVE or CAPTURE.
     * 
     * @return true if user interface get updates. Unless, false.
     */
    boolean move(Move move) {
        if(move.type == null || move.type == MoveType.NONE) return false;

        if(move.type == MoveType.CAPTURE) {
            Circle capturedCircle = circles.get(move.captured.toString());
            capturedCircle.setFill(COLOR_FILL_PIECE_DEFAULT);
        } 
        Circle source = circles.get(move.from.toString());
        Circle target = circles.get(move.to.toString());
        Piece targetPiece = game.getPiece(move.to);
        source.setFill(COLOR_FILL_PIECE_DEFAULT);
        target.setFill(getFill(targetPiece));

        /** if this session is online and current user makes a move,
         *  send to server */
        if(ONLINE_MODE && move.doerID == player.ID) {
            Message message = new Message(move.doerID, MessageType.MOVE, move);
            app.clientInstance().send(message);
        }

        for (Player p : game.getPlayers()) {
            if ( game.isDefeated(p) ) {
                gameOver();
            }
        }
        return true;
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

        double[] layouts = calculateLayout(segment, level);

        circle.setLayoutX(layouts[0]);
        circle.setLayoutY(layouts[1]);

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

    private double[] calculateLayout(Segment segment, int level) {
        final double straight = level*COEFFICIENT_STRAIGHT;
        final double angle = level*COEFFICIENT_IN_ANGLES;

        double[] layouts = new double[2]; 
        switch(segment) {
            case A:
                layouts[0] = 0;
                layouts[1] = -straight;
                break;
            case B:
                layouts[0] = angle;
                layouts[1] = -angle;
                break;
            case C:
                layouts[0] = straight;
                layouts[1] = 0;
                break;
            case D:
                layouts[0] = angle;
                layouts[1] = angle;
                break;
            case E:
                layouts[0] = 0;
                layouts[1] = +straight;
                break;
            case F:
                layouts[0] = -angle;
                layouts[1] = angle;
                break;
            case G:
                layouts[0] = -straight;
                layouts[1] = 0;
                break;
            case H:
                layouts[0] = -angle;
                layouts[1] = -angle;
                break;
            default:
                layouts[0] = 0;
                layouts[1] = 0;
                break;
        }
        return layouts;
    }

    /** Registering listeners to Circle. */
    private void activateDragAndDrop(Circle circle) {
        // src: docs.oracle.com/javafx/2/drag_drop/HelloDragAndDrop.java.html

        // source
        circle.setOnDragDetected(e -> {
            /** if active player is trying to make a move, let it drag. */
            String source= circle.getAccessibleText();
            Location location = locations.get(source);
            Piece piece = game.getPiece(location);
            if(game.activePlayer().pieceType == piece.getType() && 
                (!ONLINE_MODE || (ONLINE_MODE && game.activePlayer().ID == player.ID))) {
                /* allow any transfer mode */
                Dragboard db = circle.startDragAndDrop(TransferMode.ANY);

                /* put a string on dragboard */
                ClipboardContent content = new ClipboardContent();
                content.putString(circle.getAccessibleText());
                db.setContent(content);
            }

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

                game.getPlayers().stream().filter(p -> p.pieces.contains(sourcePiece)).findFirst().ifPresent(player -> {
                    String targetLoc = circle.getAccessibleText();
                    Location targetLocation = locations.get(targetLoc);
    
                    Move move = new Move().doer(player.ID).from(sourceLocation).to(targetLocation);

                    MoveType moveType = game.determineMoveType(move);
                    
                    if(moveType == null || moveType == MoveType.NONE) 
                        circle.setFill(COLOR_FILL_PIECE_NOT_AVALIABLE);
                    else
                        circle.setFill(COLOR_FILL_PIECE_AVALIABLE);
                });
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

                game.getPlayers().stream().filter(p -> p.pieces.contains(sourcePiece)).findFirst().ifPresent(player -> {

                    String targetLoc = circle.getAccessibleText();
                    Location targetLocation = locations.get(targetLoc);

                    Move move = new Move().doer(player.ID).from(sourceLocation).to(targetLocation);

                    boolean result = game.move(move);

                    if(result) move(move);
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

}