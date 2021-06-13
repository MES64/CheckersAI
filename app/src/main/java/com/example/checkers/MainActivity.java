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
    private HashSet<int[]> boardSquares = new HashSet<int[]>();
    private ArrayList<String> movablePieces;
    private ArrayList<String> destinationSquares = new ArrayList<String>();
    private ArrayList<GameState> childNodes;

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

        // Board Squares (int[] version of board hash set)
        for (String square : board) {
            int x = Character.getNumericValue(square.charAt(1));
            int y = Character.getNumericValue(square.charAt(3));
            boardSquares.add(new int[]{x, y});
        }

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
        // ToDo Decide move chosen outside of the cpuMove(); use global variable
        // For jump chain, do not want to figure out best move again; fine otherwise!
        // Could output best move as a sequence of coords (at least 2); global
        // Loop through this sequence to implement each pause and move
        // Do this recursively to make sure pause works; pass in current index
        // Stop when index is at the end
        MovePiece moveChosen = decideCpuMove();

        // Get info
        String fromCoords = moveChosen.fromSquare.getTag().toString();
        String toCoords = moveChosen.toSquare.getTag().toString();

        move(moveChosen.fromSquare, moveChosen.toSquare);
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
            Resources res = getResources();
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

    private MovePiece decideCpuMove() {
        // Random Move /////////////////////////////////////////////////////////////////////////////
        /*
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
         */
        ////////////////////////////////////////////////////////////////////////////////////////////

        // Use recursion:
        // - Start at current game state: red pieces, white pieces, and the current color
        // - Find movable pieces first, as limited by whether jump is possible (a kind-of pruning!)
        // - Iterate through each piece and for each move found, iteratively call minimax function
        //   which stores the next game state; store game state in a node object (n) (see website)
        // - leaf(n) - is node n a leaf?
        // - evaluate(n) - evaluate node n based on the number of pieces, and maybe the number of
        //                 movable pieces (maybe pieces that cannot move diagonally are worth less,
        //                 or maybe worth even less if by an opponent's piece) -> do at leaf/depth=0
        // - isMaxNode(n) - (optional) to check if it is a max node or min node (check current color)
        // - Can avoid calculating game states if pruned, so maybe evaluate all child nodes somehow
        //   and order them in terms of their evaluation

        // - In this function, loop through each possible move and update v as well as current move
        // - The current move at the end is the best move (with maximum v) and is returned
        // - In minimax(node n, int depth, int min, int max), only the value is returned

        // If jump chain, need multiple nodes for cases where there is a choice (cannot guarantee
        // condensing the entire chain to one node, but can guarantee having every move in a node;
        // could condense a single choice chain down to one node, and only split if a choice appears)

        // Evaluate based on number of pieces:

        // Basic:
        // - +1 for every piece you have, -1 for every piece your opponent has
        // - min = -# pieces per side
        // - max = # pieces per side
        // - Maybe king pieces too:
        // - min = -2 * # pieces per side
        // - max = +2 * # pieces per side

        // Detailed:
        // - Piece count (+1 you, -1 other)
        // - King count (+1 you, -1 other)
        // - Trapped kings
        // - Who's turn it is
        // - Runaway checkers (clear run to be kinged)
        // - Any other minor factors?:
        // -> Exposed checkers
        // -> Protected checkers
        // -> Center control

        // Change how pieces on the board are stored; String -> int[] (maybe change everywhere later)
        HashSet<int[]> currentReds = new HashSet<int[]>();
        HashSet<int[]> currentWhites = new HashSet<int[]>();
        HashSet<int[]> currentKings = new HashSet<int[]>();

        for (String piece : redPieces) {
            int x = Character.getNumericValue(piece.charAt(1));
            int y = Character.getNumericValue(piece.charAt(3));
            currentReds.add(new int[]{x, y});
        }
        for (String piece : whitePieces) {
            int x = Character.getNumericValue(piece.charAt(1));
            int y = Character.getNumericValue(piece.charAt(3));
            currentWhites.add(new int[]{x, y});
        }
        for (String king : kings) {
            int x = Character.getNumericValue(king.charAt(1));
            int y = Character.getNumericValue(king.charAt(3));
            currentKings.add(new int[]{x, y});
        }

        GameState currentState = new GameState(currentReds, currentWhites, currentKings, currentColor);

        // Set depth
        int depth = 5;

        int piecesPerSide = 12;
        int min = -2 * piecesPerSide;
        int max = 2 * piecesPerSide;

        Resources res = getResources();
        int bestGameValue = min-1;  // -1 to guarantee bestMove is written from first one found
        MovePiece bestMove = null;

        // Current node will be a max node; as it is the CPU's turn
        for (String fromCoords : movablePieces) {
            int fromId = res.getIdentifier(fromCoords, "id", getPackageName());
            ImageView fromSquare = findViewById(fromId);
            findMoves(fromSquare);

            int fromX = Character.getNumericValue(fromCoords.charAt(1));
            int fromY = Character.getNumericValue(fromCoords.charAt(3));
            int[] fromCoordsArr = new int[]{fromX, fromY};

            for (String toCoords : destinationSquares) {
                int toX = Character.getNumericValue(toCoords.charAt(1));
                int toY = Character.getNumericValue(toCoords.charAt(3));
                int[] toCoordsArr = new int[]{toX, toY};

                // Create game tree node
                // Create child with move found
                GameState child = createChild(currentState, fromCoordsArr, toCoordsArr, moveTier);

                childNodes = new ArrayList<GameState>();
                if (moveTier == 2) {  // For Jump Chains
                    childrenAfterJumpChain(child, toCoordsArr);
                }
                else {
                    childNodes.add(child);
                }
                ArrayList<GameState> currentChildNodes = new ArrayList<GameState>(childNodes);

                for (GameState finalChild : currentChildNodes) {
                    // End turn; next node in game tree
                    finalChild.currentColor = (finalChild.currentColor == 'R') ? 'W' : 'R';

                    int gameValue = minimax(finalChild, depth-1, bestGameValue, max);
                    if (gameValue > bestGameValue) {
                        bestGameValue = gameValue;

                        int toId = res.getIdentifier(toCoords, "id", getPackageName());
                        ImageView toSquare = findViewById(toId);
                        bestMove = new MovePiece(fromSquare, toSquare);
                    }
                }
            }
        }

        return bestMove;
    }

    private int minimax(GameState currentState, int depth, int min, int max) {
        MoveInfo moveInfo = getMovablePieces(currentState);

        boolean isLeaf = (moveInfo.currentMoveTier == 0);
        if (isLeaf || depth == 0) return evaluate(currentState, isLeaf);

        int bestGameValue;
        if (isMaxNode(currentState)) {
            bestGameValue = min;

            for (int[] fromCoords : moveInfo.simulatedMovablePieces) {
                ArrayList<int[]> simulatedDestinationSquares = getMoves(currentState, fromCoords, moveInfo.currentMoveTier);

                for (int[] toCoords : simulatedDestinationSquares) {
                    GameState child = createChild(currentState, fromCoords, toCoords, moveInfo.currentMoveTier);

                    childNodes = new ArrayList<GameState>();
                    if (moveInfo.currentMoveTier == 2) {  // For Jump Chains
                        childrenAfterJumpChain(child, toCoords);
                    }
                    else {
                        childNodes.add(child);
                    }
                    ArrayList<GameState> currentChildNodes = new ArrayList<GameState>(childNodes);

                    for (GameState finalChild : currentChildNodes) {
                        // End turn; next node in game tree
                        finalChild.currentColor = (finalChild.currentColor == 'R') ? 'W' : 'R';

                        int gameValue = minimax(finalChild, depth-1, bestGameValue, max);
                        if (gameValue > bestGameValue) bestGameValue = gameValue;
                        if (bestGameValue > max) return max;
                    }
                }
            }
        }
        else {  // is min node
            bestGameValue = max;

            for (int[] fromCoords : moveInfo.simulatedMovablePieces) {
                ArrayList<int[]> simulatedDestinationSquares = getMoves(currentState, fromCoords, moveInfo.currentMoveTier);

                for (int[] toCoords : simulatedDestinationSquares) {
                    GameState child = createChild(currentState, fromCoords, toCoords, moveInfo.currentMoveTier);

                    childNodes = new ArrayList<GameState>();
                    if (moveInfo.currentMoveTier == 2) {  // For Jump Chains
                        childrenAfterJumpChain(child, toCoords);
                    }
                    else {
                        childNodes.add(child);
                    }
                    ArrayList<GameState> currentChildNodes = new ArrayList<GameState>(childNodes);

                    for (GameState finalChild : currentChildNodes) {
                        // End turn; next node in game tree
                        finalChild.currentColor = (finalChild.currentColor == 'R') ? 'W' : 'R';

                        int gameValue = minimax(finalChild, depth-1, min, bestGameValue);
                        if (gameValue < bestGameValue) bestGameValue = gameValue;
                        if (bestGameValue < min) return min;
                    }
                }
            }
        }

        return bestGameValue;
    }

    private void childrenAfterJumpChain(GameState state, int[] fromCoords) {
        ArrayList<int[]> jumpMoves = getMoves(state, fromCoords, 2);

        if (jumpMoves.size() == 0) {
            childNodes.add(state);
        }
        else {
            for (int[] toCoords : jumpMoves) {
                // Create child; currentMoveTier must be 2
                GameState child = createChild(state, fromCoords, toCoords, 2);

                childrenAfterJumpChain(child, toCoords);
            }
        }
    }

    private GameState createChild(GameState state, int[] fromCoords, int[] toCoords, int currentMoveTier) {
        GameState child = new GameState(state);

        // Move
        if (child.currentColor == 'R') {
            child.redPieces.remove(fromCoords);
            child.redPieces.add(toCoords);
        }
        else {  // child.currentColor == 'W'
            child.whitePieces.remove(fromCoords);
            child.whitePieces.add(toCoords);
        }

        // Update king if needed
        if (child.kings.contains(fromCoords)) {
            child.kings.remove(fromCoords);
            child.kings.add(toCoords);
        }

        // Add king if needed
        if (!child.kings.contains(toCoords)) {
            if (dir == 1) {
                if ((child.currentColor == 'R') && (toCoords[1] == 1)) {
                    child.kings.add(toCoords);
                }
                else if ((child.currentColor == 'W') && (toCoords[1] == 8)) {
                    child.kings.add(toCoords);
                }
            }
            else {  // dir == -1
                if ((child.currentColor == 'R') && (toCoords[1] == 8)) {
                    child.kings.add(toCoords);
                }
                else if ((child.currentColor == 'W') && (toCoords[1] == 1)) {
                    child.kings.add(toCoords);
                }
            }
        }

        // Delete if needed
        if (currentMoveTier == 2) {
            // Find other coords
            int otherX = (toCoords[0] + fromCoords[0])/2;
            int otherY = (toCoords[1] + fromCoords[1])/2;
            int[] otherCoords = new int[]{otherX, otherY};

            if (child.currentColor == 'R') {
                child.whitePieces.remove(otherCoords);
            }
            else {  // child.currentColor == 'W'
                child.redPieces.remove(otherCoords);
            }

            // Remove from kings if needed
            child.kings.remove(otherCoords);
        }

        return child;
    }

    private ArrayList<int[]> getMoves(GameState state, int[] fromCoords, int currentMoveTier) {
        ArrayList<int[]> simulatedDestinationSquares = new ArrayList<int[]>();

        if (currentMoveTier == 2) {
            // Find all jumps
            // try different toCoords
            int toY = (state.currentColor == 'R') ? fromCoords[1] - 2*dir : fromCoords[1] + 2*dir;
            int[] toSquareLeft = new int[]{fromCoords[0]-2, toY};
            int[] toSquareRight = new int[]{fromCoords[0]+2, toY};

            // other piece coords
            int otherY = (state.currentColor == 'R') ? fromCoords[1]-dir : fromCoords[1]+dir;
            int[] otherSquareLeft = new int[]{fromCoords[0]-1, otherY};
            int[] otherSquareRight = new int[]{fromCoords[0]+1, otherY};

            // Check that other piece is opposite to from color and to square is empty
            // Can use hashset instead of views!
            if ((boardSquares.contains(toSquareLeft)) && (!state.whitePieces.contains(toSquareLeft)) && (!state.redPieces.contains(toSquareLeft))) {
                if ((state.currentColor == 'R') && (state.whitePieces.contains(otherSquareLeft))) simulatedDestinationSquares.add(toSquareLeft);
                if ((state.currentColor == 'W') && (state.redPieces.contains(otherSquareLeft))) simulatedDestinationSquares.add(toSquareLeft);
            }
            if ((boardSquares.contains(toSquareRight)) && (!state.whitePieces.contains(toSquareRight)) && (!state.redPieces.contains(toSquareRight))) {
                if ((state.currentColor == 'R') && (state.whitePieces.contains(otherSquareRight))) simulatedDestinationSquares.add(toSquareRight);
                if ((state.currentColor == 'W') && (state.redPieces.contains(otherSquareRight))) simulatedDestinationSquares.add(toSquareRight);
            }

            // If it is a king piece, check the other way too
            if (state.kings.contains(fromCoords)) {
                // try different toCoords
                int toYBack = (state.currentColor == 'R') ? fromCoords[1] + 2*dir : fromCoords[1] - 2*dir;
                int[] toSquareLeftBack = new int[]{fromCoords[0]-2, toYBack};
                int[] toSquareRightBack = new int[]{fromCoords[0]+2, toYBack};

                // other piece coords
                int otherYBack = (state.currentColor == 'R') ? fromCoords[1]+dir : fromCoords[1]-dir;
                int[] otherSquareLeftBack = new int[]{fromCoords[0]-1, otherYBack};
                int[] otherSquareRightBack = new int[]{fromCoords[0]+1, otherYBack};

                // Check that other piece is opposite to "from color" and "to square" is empty
                if ((boardSquares.contains(toSquareLeftBack)) && (!state.whitePieces.contains(toSquareLeftBack)) && (!state.redPieces.contains(toSquareLeftBack))) {
                    if ((state.currentColor == 'R') && (state.whitePieces.contains(otherSquareLeftBack))) simulatedDestinationSquares.add(toSquareLeftBack);
                    if ((state.currentColor == 'W') && (state.redPieces.contains(otherSquareLeftBack))) simulatedDestinationSquares.add(toSquareLeftBack);
                }
                if ((boardSquares.contains(toSquareRightBack)) && (!state.whitePieces.contains(toSquareRightBack)) && (!state.redPieces.contains(toSquareRightBack))) {
                    if ((state.currentColor == 'R') && (state.whitePieces.contains(otherSquareRightBack))) simulatedDestinationSquares.add(toSquareRightBack);
                    if ((state.currentColor == 'W') && (state.redPieces.contains(otherSquareRightBack))) simulatedDestinationSquares.add(toSquareRightBack);
                }
            }
        }
        else {  // moveTier == 1 (0 is game over)
            // Find all diagonal moves
            // try different toCoords
            int toY = (state.currentColor == 'R') ? fromCoords[1]-dir : fromCoords[1]+dir;

            int[] toSquareLeft = new int[]{fromCoords[0]-1, toY};
            int[] toSquareRight = new int[]{fromCoords[0]+1, toY};

            if ((boardSquares.contains(toSquareLeft)) && (!state.whitePieces.contains(toSquareLeft)) && (!state.redPieces.contains(toSquareLeft))) {
                simulatedDestinationSquares.add(toSquareLeft);
            }
            if ((boardSquares.contains(toSquareRight)) && (!state.whitePieces.contains(toSquareRight)) && (!state.redPieces.contains(toSquareRight))) {
                simulatedDestinationSquares.add(toSquareRight);
            }

            // If it is a king piece, check the other way too
            if (state.kings.contains(fromCoords)) {
                // try different toCoords
                int toYBack = (state.currentColor == 'R') ? fromCoords[1]+dir : fromCoords[1]-dir;

                int[] toSquareLeftBack = new int[]{fromCoords[0]-1, toYBack};
                int[] toSquareRightBack = new int[]{fromCoords[0]+1, toYBack};

                if ((boardSquares.contains(toSquareLeftBack)) && (!state.whitePieces.contains(toSquareLeftBack)) && (!state.redPieces.contains(toSquareLeftBack))) {
                    simulatedDestinationSquares.add(toSquareLeftBack);
                }
                if ((boardSquares.contains(toSquareRightBack)) && (!state.whitePieces.contains(toSquareRightBack)) && (!state.redPieces.contains(toSquareRightBack))) {
                    simulatedDestinationSquares.add(toSquareRightBack);
                }
            }
        }

        return simulatedDestinationSquares;
    }

    private MoveInfo getMovablePieces(GameState state) {
        // Get the pieces that can move, while also determining if any moves are available
        int currentMoveTier = 0;
        ArrayList<int[]> simulatedMovablePieces = new ArrayList<int[]>();
        HashSet<int[]> currentPieces = (state.currentColor == 'R') ? state.redPieces : state.whitePieces;

        for (int[] piece : currentPieces) {
            // Check for jumps
            if (canPieceJump(state, piece)) {
                if (currentMoveTier < 2) {
                    currentMoveTier = 2;
                    simulatedMovablePieces = new ArrayList<int[]>();
                }
                simulatedMovablePieces.add(piece);
            }

            // Check for diagonal moves
            if ((currentMoveTier < 2) && (canPieceMoveDiagonally(state, piece))) {
                if (currentMoveTier < 1) currentMoveTier = 1;
                simulatedMovablePieces.add(piece);
            }
        }

        return new MoveInfo(currentMoveTier, simulatedMovablePieces);
    }

    private int evaluate(GameState state, boolean isLeaf) {
        if (isLeaf) {
            int piecesPerSide = 12;
            if (state.currentColor == playerColor) {  // CPU Wins
                return 2 * piecesPerSide;  // return max
            }
            else {  // CPU Loses
                return -2 * piecesPerSide;  // return min
            }
        }

        // Pieces
        int value = state.redPieces.size() - state.whitePieces.size();

        // Kings
        for (int[] king : state.kings) {
            if (state.redPieces.contains(king)) value++;
            else                                value--;
        }

        if (playerColor == 'R') value *= -1;

        return value;
    }

    private boolean isMaxNode(GameState state) {
        // Look at current color
        if (state.currentColor == playerColor) return false;
        return true;
    }

    private boolean canPieceJump(GameState state, int[] piece) {
        // try different toCoords
        int toY = (state.currentColor == 'R') ? piece[1] - 2*dir : piece[1] + 2*dir;
        int[] toSquareLeft = new int[]{piece[0]-2, toY};
        int[] toSquareRight = new int[]{piece[0]+2, toY};

        // other piece coords
        int otherY = (state.currentColor == 'R') ? piece[1]-dir : piece[1]+dir;
        int[] otherSquareLeft = new int[]{piece[0]-1, otherY};
        int[] otherSquareRight = new int[]{piece[0]+1, otherY};

        // Check that other piece is opposite to "from color" and "to square" is empty
        // Can use hashset instead of views!
        if ((boardSquares.contains(toSquareLeft)) && (!state.whitePieces.contains(toSquareLeft)) && (!state.redPieces.contains(toSquareLeft))) {
            if ((state.currentColor == 'R') && (state.whitePieces.contains(otherSquareLeft))) return true;
            if ((state.currentColor == 'W') && (state.redPieces.contains(otherSquareLeft))) return true;
        }
        if ((boardSquares.contains(toSquareRight)) && (!state.whitePieces.contains(toSquareRight)) && (!state.redPieces.contains(toSquareRight))) {
            if ((state.currentColor == 'R') && (state.whitePieces.contains(otherSquareRight))) return true;
            if ((state.currentColor == 'W') && (state.redPieces.contains(otherSquareRight))) return true;
        }

        // If it is a king piece, check the other way too
        if (state.kings.contains(piece)) {
            // try different toCoords
            int toYBack = (state.currentColor == 'R') ? piece[1] + 2*dir : piece[1] - 2*dir;
            int[] toSquareLeftBack = new int[]{piece[0]-2, toYBack};
            int[] toSquareRightBack = new int[]{piece[0]+2, toYBack};

            // other piece coords
            int otherYBack = (state.currentColor == 'R') ? piece[1]+dir : piece[1]-dir;
            int[] otherSquareLeftBack = new int[]{piece[0]-1, otherYBack};
            int[] otherSquareRightBack = new int[]{piece[0]+1, otherYBack};

            // Check that other piece is opposite to "from color" and "to square" is empty
            if ((boardSquares.contains(toSquareLeftBack)) && (!state.whitePieces.contains(toSquareLeftBack)) && (!state.redPieces.contains(toSquareLeftBack))) {
                if ((state.currentColor == 'R') && (state.whitePieces.contains(otherSquareLeftBack))) return true;
                if ((state.currentColor == 'W') && (state.redPieces.contains(otherSquareLeftBack))) return true;
            }
            if ((boardSquares.contains(toSquareRightBack)) && (!state.whitePieces.contains(toSquareRightBack)) && (!state.redPieces.contains(toSquareRightBack))) {
                if ((state.currentColor == 'R') && (state.whitePieces.contains(otherSquareRightBack))) return true;
                if ((state.currentColor == 'W') && (state.redPieces.contains(otherSquareRightBack))) return true;
            }
        }

        return false;
    }

    private boolean canPieceMoveDiagonally(GameState state, int[] piece) {
        // try different toCoords
        int toY = (state.currentColor == 'R') ? piece[1]-dir : piece[1]+dir;

        int[] toSquareLeft = new int[]{piece[0]-1, toY};
        int[] toSquareRight = new int[]{piece[0]+1, toY};

        if ((boardSquares.contains(toSquareLeft)) && (!state.whitePieces.contains(toSquareLeft)) && (!state.redPieces.contains(toSquareLeft))) {
            return true;
        }
        if ((boardSquares.contains(toSquareRight)) && (!state.whitePieces.contains(toSquareRight)) && (!state.redPieces.contains(toSquareRight))) {
            return true;
        }

        // If it is a king piece, check the other way too
        if (state.kings.contains(piece)) {
            // try different toCoords
            int toYBack = (state.currentColor == 'R') ? piece[1]+dir : piece[1]-dir;

            int[] toSquareLeftBack = new int[]{piece[0]-1, toYBack};
            int[] toSquareRightBack = new int[]{piece[0]+1, toYBack};

            if ((boardSquares.contains(toSquareLeftBack)) && (!state.whitePieces.contains(toSquareLeftBack)) && (!state.redPieces.contains(toSquareLeftBack))) {
                return true;
            }
            if ((boardSquares.contains(toSquareRightBack)) && (!state.whitePieces.contains(toSquareRightBack)) && (!state.redPieces.contains(toSquareRightBack))) {
                return true;
            }
        }

        return false;
    }

    private static class GameState {
        public HashSet<int[]> redPieces;
        public HashSet<int[]> whitePieces;
        public HashSet<int[]> kings;
        public char currentColor;

        public GameState(HashSet<int[]> redPieces, HashSet<int[]> whitePieces, HashSet<int[]> kings, char currentColor) {
            this.redPieces = redPieces;
            this.whitePieces = whitePieces;
            this.kings = kings;
            this.currentColor = currentColor;
        }

        public GameState(GameState originalState) {
            this.redPieces = new HashSet<int[]>(originalState.redPieces);
            this.whitePieces = new HashSet<int[]>(originalState.whitePieces);
            this.kings = new HashSet<int[]>(originalState.kings);
            this.currentColor = originalState.currentColor;
        }
    }

    private class MoveInfo {
        public int currentMoveTier;
        public ArrayList<int[]> simulatedMovablePieces;

        public MoveInfo(int currentMoveTier, ArrayList<int[]> simulatedMovablePieces) {
            this.currentMoveTier = currentMoveTier;
            this.simulatedMovablePieces = simulatedMovablePieces;
        }
    }

    private class MovePiece {
        public ImageView fromSquare;
        public ImageView toSquare;

        public MovePiece(ImageView fromSquare, ImageView toSquare) {
            this.fromSquare = fromSquare;
            this.toSquare = toSquare;
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