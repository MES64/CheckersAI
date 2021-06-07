package com.example.checkers;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private Integer storedSquare = null;
    private char currentColor = 'R';
    private char playerColor;
    private int moveTier;
    private int dir;
    private String gameMode;
    private HashSet<String> whitePieces = new HashSet<String>();
    private HashSet<String> redPieces = new HashSet<String>();
    private HashSet<String> kings = new HashSet<String>();
    private HashSet<String> board = new HashSet<String>();
    private ArrayList<String> movablePieces;
    private ArrayList<String> destinationSquares = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Randomly pick between white and red to go in front (who gets which color?)
        // Create variable dir to set the direction of forwards for each color
        Random random = new Random();
        dir = 2*random.nextInt(2) - 1;  // Pick [-1,1] with equal probability

        Resources res = getResources();
        // Set up images, whitePieces, redPieces; depends on direction dir
        if (dir == 1) {  // white at bottom of screen
            // Add pieces to white and red sets of pieces
            whitePieces.add("C1_1");
            whitePieces.add("C3_1");
            whitePieces.add("C5_1");
            whitePieces.add("C7_1");
            whitePieces.add("C2_2");
            whitePieces.add("C4_2");
            whitePieces.add("C6_2");
            whitePieces.add("C8_2");
            whitePieces.add("C1_3");
            whitePieces.add("C3_3");
            whitePieces.add("C5_3");
            whitePieces.add("C7_3");

            redPieces.add("C8_8");
            redPieces.add("C6_8");
            redPieces.add("C4_8");
            redPieces.add("C2_8");
            redPieces.add("C7_7");
            redPieces.add("C5_7");
            redPieces.add("C3_7");
            redPieces.add("C1_7");
            redPieces.add("C8_6");
            redPieces.add("C6_6");
            redPieces.add("C4_6");
            redPieces.add("C2_6");

            // Set up images
            // Do nothing (default)
        }
        else {  // dir == -1 : red at bottom of screen
            // Add pieces to white and red sets of pieces
            redPieces.add("C1_1");
            redPieces.add("C3_1");
            redPieces.add("C5_1");
            redPieces.add("C7_1");
            redPieces.add("C2_2");
            redPieces.add("C4_2");
            redPieces.add("C6_2");
            redPieces.add("C8_2");
            redPieces.add("C1_3");
            redPieces.add("C3_3");
            redPieces.add("C5_3");
            redPieces.add("C7_3");

            whitePieces.add("C8_8");
            whitePieces.add("C6_8");
            whitePieces.add("C4_8");
            whitePieces.add("C2_8");
            whitePieces.add("C7_7");
            whitePieces.add("C5_7");
            whitePieces.add("C3_7");
            whitePieces.add("C1_7");
            whitePieces.add("C8_6");
            whitePieces.add("C6_6");
            whitePieces.add("C4_6");
            whitePieces.add("C2_6");

            // Set up images
            for (String piece : redPieces) {
                int pieceId = res.getIdentifier(piece, "id", getPackageName());
                ImageView pieceView = findViewById(pieceId);
                pieceView.setImageResource(R.drawable.red_checker);
            }
            for (String piece : whitePieces) {
                int pieceId = res.getIdentifier(piece, "id", getPackageName());
                ImageView pieceView = findViewById(pieceId);
                pieceView.setImageResource(R.drawable.white_checker);
            }
        }

        // Add all squares to board
        // Red and white piece squares
        for (String piece : whitePieces) board.add(piece);
        for (String piece : redPieces) board.add(piece);

        // Middle (unoccupied) squares
        board.add("C2_4");
        board.add("C4_4");
        board.add("C6_4");
        board.add("C8_4");
        board.add("C1_5");
        board.add("C3_5");
        board.add("C5_5");
        board.add("C7_5");

        // Disable all click events
        for (String square : board) {
            int squareId = res.getIdentifier(square, "id", getPackageName());
            ImageView squareView = findViewById(squareId);
            squareView.setEnabled(false);
        }

        // Get game mode from intent
        Intent i = getIntent();
        gameMode = i.getStringExtra("mode");

        if (gameMode.equals("One-Player")) {
            if (dir == 1) playerColor = 'W';
            else playerColor = 'R';
        }

        setUpNextTurn();

        // Create event listeners based on game mode
        /*
        if (gameMode.equals("One-Player")) {
            // Create click listener
            View.OnClickListener btnClick = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    playVsCPU(v);
                }
            };

            for (String square : board) {
                int squareId = res.getIdentifier(square, "id", getPackageName());
                ImageView squareView = findViewById(squareId);
                squareView.setOnClickListener(btnClick);
            }

            if (dir == 1) {
                currentColor = 'W';
                // do cpu stuff
            }
            else {  // dir == -1
                currentColor = 'R';
                setUpNextTurn();
            }
        }
        else {  // gameMode == "Two-Player"
            // Disable all click events
            for (String square : board) {
                int squareId = res.getIdentifier(square, "id", getPackageName());
                ImageView squareView = findViewById(squareId);
                squareView.setEnabled(false);
            }

            setUpNextTurn();
        }
         */
    }

    // ToDo
    // AI
    // Randomly decide who goes first
    // Set up board according to color; red goes first; rotate layout? or set params of squares?
    // Chain taking; must complete all jumps
    // Make jumping pieces, if available, compulsory! Can choose if multiple
    // Kinging; King image
    // Piece selected and available images
    // Unselect piece by clicking itself again
    // Detect if a player has lost (no available moves on their turn; HashSet of pieces)
    // Home or Settings Activity; difficulty
    // Quit button; if game cannot be won the game is cancelled/drawn
    // Draw detection complicated, so will leave it to the user; can do if want...

    // Movement:
    // Have a variable called moveTier = 0/1/2
    // moveTier == 0 => No moves found so far
    // moveTier == 1 => Only diagonal moves found so far
    // moveTier == 2 => Jump moves found
    // Check through all pieces and check through all moves based on moveTier; add to different
    // hashset
    // E.g. if moveTier == 0 and diagonal move found then add piece and set moveTier to 1
    //      if moveTier == 1 and diagonal move found then just add piece
    //      if moveTier == 1 and jump found then reset hashset, add piece, and set moveTier to 2
    //      if moveTier == 2 then do not check for diagonal moves; only jumps are checked
    // If moveTier == 0 at the end => this player loses
    // If moveTier == 1 at the end => choose from diagonal moves (highlight pieces can move)
    // If moveTier == 2 at the end => choose from jumps (highlight pieces can move)
    // If chain of jumps, keep current piece highlighted until no more jumps available

    // Effects:
    // Highlight pieces that can move
    // Brighter highlight for piece selected
    // Red flash if selected square is not acceptable
    // Letter 'K' on King pieces (Insert new char at index 1 to denote if regular Piece or King)
    // Need a pause method, I think (especially for the AI)

    // AI:
    // Alpha-Beta; game-tree
    // Difficulty settings based on search depth

    // Update:
    // Just setClickable() to set clickable to true/false
    // As well as using hashsets, set these up
    // No need to check in hashsets then

    /*
    public void playVsCPU(View v) {
        // For 1-player mode

        ImageView square = (ImageView)v;
        String squareCoords = square.getTag().toString();
        if (redPieces.contains(squareCoords))        square.setImageResource(R.drawable.red_checker_movable);
        else if (whitePieces.contains(squareCoords)) square.setImageResource(R.drawable.white_checker_movable);
        else                                         square.setImageResource(R.drawable.black_square_highlighted);
    }
     */

    public void moveChecker(View v) {
        if (storedSquare == null) {
            storedSquare = v.getId();
            selectPiece((ImageView)v);
        }
        else {
            ImageView fromSquare = findViewById(storedSquare);
            ImageView toSquare = (ImageView)v;

            // Make the pieces clickable or non-clickable (update when highlighting and unhighlighting)
            deselectPiece();
            if (fromSquare != toSquare) {
                // Whole board then has normal coloring; can now swap images
                unhighlightPieces();

                move(fromSquare, toSquare);
                if (moveTier == 2) {  // Jump
                    // toSquare must be valid, so execute move and end turn
                    String fromCoords = fromSquare.getTag().toString();
                    int fromX = Character.getNumericValue(fromCoords.charAt(1));
                    int fromY = Character.getNumericValue(fromCoords.charAt(3));

                    String toCoords = toSquare.getTag().toString();
                    int toX = Character.getNumericValue(toCoords.charAt(1));
                    int toY = Character.getNumericValue(toCoords.charAt(3));

                    // Other (middle) piece info
                    int otherPieceX = (toX + fromX)/2;
                    int otherPieceY = (toY + fromY)/2;
                    String otherPieceCoords = "C" + otherPieceX + "_" + otherPieceY;

                    // otherPiece view retrieved
                    Resources res = getResources();
                    int otherPieceId = res.getIdentifier(otherPieceCoords, "id", getPackageName());
                    ImageView otherPiece = findViewById(otherPieceId);

                    // Jump removes the other piece
                    remove(otherPiece);

                    // Check for more jumps
                    if (canJump(toCoords)) {
                        // relevant setUpNextTurn stuff
                        movablePieces = new ArrayList<String>();
                        movablePieces.add(toCoords);

                        storedSquare = toSquare.getId();
                        selectPiece(toSquare);
                        toSquare.setEnabled(false);
                    }
                    else {
                        currentColor = (currentColor == 'R') ? 'W' : 'R';
                        setUpNextTurn();
                        storedSquare = null;
                    }
                }
                else {  // Move Diagonally
                    currentColor = (currentColor == 'R') ? 'W' : 'R';
                    setUpNextTurn();
                    storedSquare = null;
                }
            }
            else {
                storedSquare = null;
            }
        }
    }

    private void setUpNextTurn() {
        moveTier = 0;
        movablePieces = new ArrayList<String>();
        HashSet<String> currentPieces = (currentColor == 'R') ? redPieces : whitePieces;

        for (String piece : currentPieces) {
            // Check for jumps
            if (canJump(piece)) {
                if (moveTier < 2) {
                    moveTier = 2;
                    movablePieces = new ArrayList<String>();
                }
                movablePieces.add(piece);
            }

            // Check for diagonal moves
            if ((moveTier < 2) && (canMoveDiagonally(piece))) {
                if (moveTier < 1) moveTier = 1;
                movablePieces.add(piece);
            }
        }

        if (moveTier == 0) {
            gameOver();
        }
        else {
            if (gameMode.equals("Two-Player")) {
                highlightPieces();
            }
            else {  // One-Player
                if (currentColor == playerColor) {
                    highlightPieces();
                }
                else                             {
                    // Pause before move
                    Handler h = new Handler();
                    h.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            cpuMove();
                        }
                    }, 1000);
                }
            }
        }
    }

    private void cpuMove() {
        // Decide CPU Move /////////////////////////////////////
        Random random = new Random();
        int randomIndex = random.nextInt(movablePieces.size());
        String fromCoords = movablePieces.get(randomIndex);

        Resources res = getResources();
        int fromId = res.getIdentifier(fromCoords, "id", getPackageName());
        ImageView fromSquare = findViewById(fromId);

        findMoves(fromSquare);

        randomIndex = random.nextInt(destinationSquares.size());
        String toCoords = destinationSquares.get(randomIndex);

        destinationSquares = new ArrayList<String>();  // reset

        int toId = res.getIdentifier(toCoords, "id", getPackageName());
        ImageView toSquare = findViewById(toId);
        ////////////////////////////////////////////////////////

        move(fromSquare, toSquare);
        // remove if needed
        if (moveTier == 2) {
            int fromX = Character.getNumericValue(fromCoords.charAt(1));
            int fromY = Character.getNumericValue(fromCoords.charAt(3));
            int toX = Character.getNumericValue(toCoords.charAt(1));
            int toY = Character.getNumericValue(toCoords.charAt(3));

            // Other (middle) piece info
            int otherPieceX = (toX + fromX)/2;
            int otherPieceY = (toY + fromY)/2;
            String otherPieceCoords = "C" + otherPieceX + "_" + otherPieceY;

            // otherPiece view retrieved
            int otherPieceId = res.getIdentifier(otherPieceCoords, "id", getPackageName());
            ImageView otherPiece = findViewById(otherPieceId);

            remove(otherPiece);

            // Jump Chain
            if (canJump(toCoords)) {
                movablePieces = new ArrayList<String>();
                movablePieces.add(toCoords);

                // Pause before move
                Handler h = new Handler();
                h.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        cpuMove();
                    }
                }, 1000);
            }
            else {
                // End CPU Turn
                currentColor = (currentColor == 'R') ? 'W' : 'R';
                setUpNextTurn();
            }
        }
        else {
            // End CPU Turn
            currentColor = (currentColor == 'R') ? 'W' : 'R';
            setUpNextTurn();
        }
    }

    private boolean canJump(String piece) {
        int fromX = Character.getNumericValue(piece.charAt(1));
        int fromY = Character.getNumericValue(piece.charAt(3));

        // try different toCoords
        int toXLeft = fromX-2;
        int toXRight = fromX+2;
        int toY = (currentColor == 'R') ? fromY - 2*dir : fromY + 2*dir;
        String toSquareLeft = "C" + toXLeft + "_" + toY;
        String toSquareRight = "C" + toXRight + "_" + toY;

        // other piece coords
        int otherXLeft = fromX-1;
        int otherXRight = fromX+1;
        int otherY = (currentColor == 'R') ? fromY-dir : fromY+dir;
        String otherSquareLeft = "C" + otherXLeft + "_" + otherY;
        String otherSquareRight = "C" + otherXRight + "_" + otherY;

        // Check that other piece is opposite to "from color" and "to square" is empty
        // Can use hashset instead of views!
        if ((board.contains(toSquareLeft)) && (!whitePieces.contains(toSquareLeft)) && (!redPieces.contains(toSquareLeft))) {
            if ((currentColor == 'R') && (whitePieces.contains(otherSquareLeft))) return true;
            if ((currentColor == 'W') && (redPieces.contains(otherSquareLeft))) return true;
        }
        if ((board.contains(toSquareRight)) && (!whitePieces.contains(toSquareRight)) && (!redPieces.contains(toSquareRight))) {
            if ((currentColor == 'R') && (whitePieces.contains(otherSquareRight))) return true;
            if ((currentColor == 'W') && (redPieces.contains(otherSquareRight))) return true;
        }

        // If it is a king piece, check the other way too
        if (kings.contains(piece)) {
            // try different toCoords
            int toYBack = (currentColor == 'R') ? fromY + 2*dir : fromY - 2*dir;
            String toSquareLeftBack = "C" + toXLeft + "_" + toYBack;
            String toSquareRightBack = "C" + toXRight + "_" + toYBack;

            // other piece coords
            int otherYBack = (currentColor == 'R') ? fromY+dir : fromY-dir;
            String otherSquareLeftBack = "C" + otherXLeft + "_" + otherYBack;
            String otherSquareRightBack = "C" + otherXRight + "_" + otherYBack;

            // Check that other piece is opposite to "from color" and "to square" is empty
            if ((board.contains(toSquareLeftBack)) && (!whitePieces.contains(toSquareLeftBack)) && (!redPieces.contains(toSquareLeftBack))) {
                if ((currentColor == 'R') && (whitePieces.contains(otherSquareLeftBack))) return true;
                if ((currentColor == 'W') && (redPieces.contains(otherSquareLeftBack))) return true;
            }
            if ((board.contains(toSquareRightBack)) && (!whitePieces.contains(toSquareRightBack)) && (!redPieces.contains(toSquareRightBack))) {
                if ((currentColor == 'R') && (whitePieces.contains(otherSquareRightBack))) return true;
                if ((currentColor == 'W') && (redPieces.contains(otherSquareRightBack))) return true;
            }
        }

        return false;
    }

    private boolean canMoveDiagonally(String piece) {
        int fromX = Character.getNumericValue(piece.charAt(1));
        int fromY = Character.getNumericValue(piece.charAt(3));

        // try different toCoords
        int toXLeft = fromX-1;
        int toXRight = fromX+1;
        int toY = (currentColor == 'R') ? fromY-dir : fromY+dir;

        String toSquareLeft = "C" + toXLeft + "_" + toY;
        String toSquareRight = "C" + toXRight + "_" + toY;

        if ((board.contains(toSquareLeft)) && (!whitePieces.contains(toSquareLeft)) && (!redPieces.contains(toSquareLeft))) {
            return true;
        }
        if ((board.contains(toSquareRight)) && (!whitePieces.contains(toSquareRight)) && (!redPieces.contains(toSquareRight))) {
            return true;
        }

        // If it is a king piece, check the other way too
        if (kings.contains(piece)) {
            // try different toCoords
            int toYBack = (currentColor == 'R') ? fromY+dir : fromY-dir;

            String toSquareLeftBack = "C" + toXLeft + "_" + toYBack;
            String toSquareRightBack = "C" + toXRight + "_" + toYBack;

            if ((board.contains(toSquareLeftBack)) && (!whitePieces.contains(toSquareLeftBack)) && (!redPieces.contains(toSquareLeftBack))) {
                return true;
            }
            if ((board.contains(toSquareRightBack)) && (!whitePieces.contains(toSquareRightBack)) && (!redPieces.contains(toSquareRightBack))) {
                return true;
            }
        }

        return false;
    }

    private void selectPiece(ImageView piece) {
        unhighlightPieces();

        // The "select" image is set
        setSelectedPieceImage(piece);

        // Find destination squares (put into the global HashSet)
        findMoves(piece);

        // Highlight destination squares
        highlightSquares();
    }

    private void deselectPiece() {
        unhighlightSquares();

        // Reset destinationSquares to be empty
        destinationSquares = new ArrayList<String>();

        highlightPieces();
    }

    private void setSelectedPieceImage(ImageView piece) {
        String pieceCoords = piece.getTag().toString();

        if (currentColor == 'R') {
            if (kings.contains(pieceCoords)) piece.setImageResource(R.drawable.red_king_checker_selected);
            else                             piece.setImageResource(R.drawable.red_checker_selected);
        }
        else {  // currentColor == 'W'
            if (kings.contains(pieceCoords)) piece.setImageResource(R.drawable.white_king_checker_selected);
            else                             piece.setImageResource(R.drawable.white_checker_selected);
        }

        piece.setEnabled(true);
    }

    private void findMoves(ImageView piece) {
        String fromCoords = piece.getTag().toString();
        int fromX = Character.getNumericValue(fromCoords.charAt(1));
        int fromY = Character.getNumericValue(fromCoords.charAt(3));

        if (moveTier == 2) {
            // Find all jumps
            // try different toCoords
            int toXLeft = fromX-2;
            int toXRight = fromX+2;
            int toY = (currentColor == 'R') ? fromY - 2*dir : fromY + 2*dir;
            String toSquareLeft = "C" + toXLeft + "_" + toY;
            String toSquareRight = "C" + toXRight + "_" + toY;

            // other piece coords
            int otherXLeft = fromX-1;
            int otherXRight = fromX+1;
            int otherY = (currentColor == 'R') ? fromY-dir : fromY+dir;
            String otherSquareLeft = "C" + otherXLeft + "_" + otherY;
            String otherSquareRight = "C" + otherXRight + "_" + otherY;

            // Check that other piece is opposite to from color and to square is empty
            // Can use hashset instead of views!
            if ((board.contains(toSquareLeft)) && (!whitePieces.contains(toSquareLeft)) && (!redPieces.contains(toSquareLeft))) {
                if ((currentColor == 'R') && (whitePieces.contains(otherSquareLeft))) destinationSquares.add(toSquareLeft);
                if ((currentColor == 'W') && (redPieces.contains(otherSquareLeft))) destinationSquares.add(toSquareLeft);
            }
            if ((board.contains(toSquareRight)) && (!whitePieces.contains(toSquareRight)) && (!redPieces.contains(toSquareRight))) {
                if ((currentColor == 'R') && (whitePieces.contains(otherSquareRight))) destinationSquares.add(toSquareRight);
                if ((currentColor == 'W') && (redPieces.contains(otherSquareRight))) destinationSquares.add(toSquareRight);
            }

            // If it is a king piece, check the other way too
            if (kings.contains(fromCoords)) {
                // try different toCoords
                int toYBack = (currentColor == 'R') ? fromY + 2*dir : fromY - 2*dir;
                String toSquareLeftBack = "C" + toXLeft + "_" + toYBack;
                String toSquareRightBack = "C" + toXRight + "_" + toYBack;

                // other piece coords
                int otherYBack = (currentColor == 'R') ? fromY+dir : fromY-dir;
                String otherSquareLeftBack = "C" + otherXLeft + "_" + otherYBack;
                String otherSquareRightBack = "C" + otherXRight + "_" + otherYBack;

                // Check that other piece is opposite to "from color" and "to square" is empty
                if ((board.contains(toSquareLeftBack)) && (!whitePieces.contains(toSquareLeftBack)) && (!redPieces.contains(toSquareLeftBack))) {
                    if ((currentColor == 'R') && (whitePieces.contains(otherSquareLeftBack))) destinationSquares.add(toSquareLeftBack);
                    if ((currentColor == 'W') && (redPieces.contains(otherSquareLeftBack))) destinationSquares.add(toSquareLeftBack);
                }
                if ((board.contains(toSquareRightBack)) && (!whitePieces.contains(toSquareRightBack)) && (!redPieces.contains(toSquareRightBack))) {
                    if ((currentColor == 'R') && (whitePieces.contains(otherSquareRightBack))) destinationSquares.add(toSquareRightBack);
                    if ((currentColor == 'W') && (redPieces.contains(otherSquareRightBack))) destinationSquares.add(toSquareRightBack);
                }
            }
        }
        else {  // moveTier == 1 (0 is game over)
            // Find all diagonal moves
            // try different toCoords
            int toXLeft = fromX-1;
            int toXRight = fromX+1;
            int toY = (currentColor == 'R') ? fromY-dir : fromY+dir;

            String toSquareLeft = "C" + toXLeft + "_" + toY;
            String toSquareRight = "C" + toXRight + "_" + toY;

            if ((board.contains(toSquareLeft)) && (!whitePieces.contains(toSquareLeft)) && (!redPieces.contains(toSquareLeft))) {
                destinationSquares.add(toSquareLeft);
            }
            if ((board.contains(toSquareRight)) && (!whitePieces.contains(toSquareRight)) && (!redPieces.contains(toSquareRight))) {
                destinationSquares.add(toSquareRight);
            }

            // If it is a king piece, check the other way too
            if (kings.contains(fromCoords)) {
                // try different toCoords
                int toYBack = (currentColor == 'R') ? fromY+dir : fromY-dir;

                String toSquareLeftBack = "C" + toXLeft + "_" + toYBack;
                String toSquareRightBack = "C" + toXRight + "_" + toYBack;

                if ((board.contains(toSquareLeftBack)) && (!whitePieces.contains(toSquareLeftBack)) && (!redPieces.contains(toSquareLeftBack))) {
                    destinationSquares.add(toSquareLeftBack);
                }
                if ((board.contains(toSquareRightBack)) && (!whitePieces.contains(toSquareRightBack)) && (!redPieces.contains(toSquareRightBack))) {
                    destinationSquares.add(toSquareRightBack);
                }
            }
        }
    }

    private void unhighlightPieces() {
        Resources res = getResources();
        if (currentColor == 'R') {
            for (String piece : movablePieces) {
                int pieceId = res.getIdentifier(piece, "id", getPackageName());
                ImageView fromPiece = findViewById(pieceId);

                if (kings.contains(piece)) fromPiece.setImageResource(R.drawable.red_king_checker);
                else                       fromPiece.setImageResource(R.drawable.red_checker);

                fromPiece.setEnabled(false);
            }
        }
        else {  // currentColor == 'W'
            for (String piece : movablePieces) {
                int pieceId = res.getIdentifier(piece, "id", getPackageName());
                ImageView fromPiece = findViewById(pieceId);

                if (kings.contains(piece)) fromPiece.setImageResource(R.drawable.white_king_checker);
                else                       fromPiece.setImageResource(R.drawable.white_checker);

                fromPiece.setEnabled(false);
            }
        }
    }

    private void highlightPieces() {
        Resources res = getResources();
        if (currentColor == 'R') {
            for (String piece : movablePieces) {
                int pieceId = res.getIdentifier(piece, "id", getPackageName());
                ImageView fromPiece = findViewById(pieceId);

                if (kings.contains(piece)) fromPiece.setImageResource(R.drawable.red_king_checker_movable);
                else                       fromPiece.setImageResource(R.drawable.red_checker_movable);

                fromPiece.setEnabled(true);
            }
        }
        else {  // currentColor == 'W'
            for (String piece : movablePieces) {
                int pieceId = res.getIdentifier(piece, "id", getPackageName());
                ImageView fromPiece = findViewById(pieceId);

                if (kings.contains(piece)) fromPiece.setImageResource(R.drawable.white_king_checker_movable);
                else                       fromPiece.setImageResource(R.drawable.white_checker_movable);

                fromPiece.setEnabled(true);
            }
        }
    }

    private void unhighlightSquares() {
        Resources res = getResources();
        for (String square : destinationSquares) {
            int squareId = res.getIdentifier(square, "id", getPackageName());
            ImageView toSquare = findViewById(squareId);
            toSquare.setImageResource(R.drawable.black_square);
            toSquare.setEnabled(false);
        }
    }

    private void highlightSquares() {
        Resources res = getResources();
        for (String square : destinationSquares) {
            int squareId = res.getIdentifier(square, "id", getPackageName());
            ImageView toSquare = findViewById(squareId);
            toSquare.setImageResource(R.drawable.black_square_highlighted);
            toSquare.setEnabled(true);
        }
    }

    private void move(ImageView fromSquare, ImageView toSquare) {
        // Set images
        Drawable fromImg = fromSquare.getDrawable();
        fromSquare.setImageResource(R.drawable.black_square);
        toSquare.setImageDrawable(fromImg);

        // Update current color piece set
        String fromCoords = fromSquare.getTag().toString();
        String toCoords = toSquare.getTag().toString();
        if (currentColor == 'R') {
            redPieces.remove(fromCoords);
            redPieces.add(toCoords);
        }
        else {  // currentColor == 'W'
            whitePieces.remove(fromCoords);
            whitePieces.add(toCoords);
        }

        // Update king if needed
        if (kings.contains(fromCoords)) {
            kings.remove(fromCoords);
            kings.add(toCoords);
        }

        // Add king if needed
        if (!kings.contains(toCoords)) {
            int toY = Character.getNumericValue(toCoords.charAt(3));

            if (dir == 1) {
                if ((currentColor == 'R') && (toY == 1)) {
                    kings.add(toCoords);
                    toSquare.setImageResource(R.drawable.red_king_checker);
                }
                else if ((currentColor == 'W') && (toY == 8)) {
                    kings.add(toCoords);
                    toSquare.setImageResource(R.drawable.white_king_checker);
                }
            }
            else {  // dir == -1
                if ((currentColor == 'R') && (toY == 8)) {
                    kings.add(toCoords);
                    toSquare.setImageResource(R.drawable.red_king_checker);
                }
                else if ((currentColor == 'W') && (toY == 1)) {
                    kings.add(toCoords);
                    toSquare.setImageResource(R.drawable.white_king_checker);
                }
            }
        }
    }

    private void remove(ImageView otherPiece) {
        // Change image to blank
        otherPiece.setImageResource(R.drawable.black_square);

        // Remove from opposite color piece set
        String otherCoords = otherPiece.getTag().toString();
        if (currentColor == 'R') {
            whitePieces.remove(otherCoords);
        }
        else {  // currentColor == 'W'
            redPieces.remove(otherCoords);
        }

        // Remove from kings if needed
        if (kings.contains(otherCoords)) kings.remove(otherCoords);
    }

    private void gameOver() {
        Log.d("Game Over", "Game Over");
        // Make text visible that says "Game Over: <!Current Color> Wins!"
        String winningColor = (currentColor == 'R') ? "White" : "Red";
        String msg = "Game Over: " + winningColor + " Wins!";
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }
}