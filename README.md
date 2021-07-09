# CheckersAI
Checkers with AI; Android (Java)

[Video Demo](https://www.youtube.com/watch?v=XITIdYNwBzk&t=1s)

![Checkers](https://user-images.githubusercontent.com/15747137/125115178-e094a200-e0e2-11eb-853b-bca51ca79f37.png)

Implemented an app to play the game Checkers, where the player’s available moves are highlighted and all the pieces can only move within the constraints of the rules. 

Tap a movable piece (highlighted on-screen) and tap an (also highlighted) destination square to complete the move. Includes 2-player and 1-player modes. 

Created an AI opponent that beats me in Checkers about 80% of the time by using an efficient brute force move searching algorithm, called minimax with alpha-beta pruning. 

## Installation

For now, you must pull the code into Android Studio and run on an emulator to give it a go yourself. 

## Implementing Checkers Constraints

### Data Structures

Red pieces, white pieces, king pieces, and the board squares are represented as a HashSet of strings containing the coordinates. This allows the program to find out if a square is contained on the board, if a square contains a piece, if it's a king piece, and what colour it is in constant time. The strings stored are identical to the tags given to each view representing the square on the board, meaning the corresponding views can be found quickly. 

### Implementation

Image views representing board squares are used, which include click event listeners. 

The current colour, representing whos turn it is, is swapped at the end of the previous turn. The corresponding HashSet is searched, looking to see what moves are available. In Checkers, if an opposing piece can be taken then it must be. So a list of pieces that are allowed to move are found and highlighted on screen. If no pieces can be moved then it is game over. 

Then those movable pieces can be selected via the event listeners and the possible destination squares are found and highlighted. The move is completed by tapping a destination square, or cancelled by tapping the selected piece. 

## AI

### Minimax with Alpha-Beta Pruning

Minimax works by searching each branch in the game tree for all possible moves until the end of the game is reached (leaf nodes). The max value is given for a winning node and the min value for a losing node. For non-leaf nodes, the value given is the maximum of it's child nodes if it is the computer's turn (max node), and the minimum if it is the other player's turn (min node). The move chosen is one with the largest value, which leads to the best possible outcome given optimal play. 

The problem is that the number of game nodes increases exponentially, so the search depth is limited to a pre-defined amount. At the given depth, if the game node is not a leaf node then the game state is evaluated to a value between min and max which depends on how winning the position is. This can be defined simply in Checkers by piece material, giving twice the score for king pieces. 

To increase the possible depth before the program becomes too slow, alpha-beta pruning is applied. Consider a node which will pick the value which is the maximum of its child node values. The first child node gives a value of 6. The second child node is searched, which is trying to find the minimum of its own child node values, with its first child node value being 3. This means that the second child node of the original node will have a value <= 3, less than the value of the first child node (6). Therefore the second child node can never be chosen over the first, and so the search involving the second child node can end. 

[Source](https://www.cs.cornell.edu/courses/cs312/2002sp/lectures/rec21.htm)

### Data Structures

Objects represent the game states in the game tree, which stores the HashSets for the red pieces, white pieces, king pieces, and the current colour. These are manipulated to create the child nodes. No references to other game states are required, as they are linked via recursion. 

### Implementation

Minimax is a recursive algorithm, so the current child nodes are passed into each recursive call of minimax. The depth is passed in to detect when to stop going deeper, and a min and max value are passed in as well to determine the boundries to stop the search (alpha-beta pruning). 

The child nodes are found using a similar process described before, by finding all movable pieces and then the destination squares for each selected piece. 

At the start of each minimax call, the node is detected to be a leaf node or not depending on if any valid moves are left. If it is a leaf node, return the max/min (win/lose) value. 

If it's not a leaf node then depending on if it is a max or min node, it loops through each child, call minimax to find it's value, and update the maximum/minimnum value to return at the end. It immediately returns the max/min value (win/lose) if it stops the search due to alpha-beta pruning. 
