# Notes
These are some notes for the my cs 240 class.

# Java
public class HelloWorld {
    public static void main(String[] args) {
        System.out.println("Hello World!");
    }
}

To compile via the command line, use "javac filename"
Then put "java classname"


# toString()
When doing the toString() for each object, here's some examples.
For each piece, print color and type.
For each move, print start and end space.
Etc.

Consider using a HashSet<ChessMove>() for pieceMoves so
you return a set of moves

toString()
    String.format("%d%d", row, col)
    String.format("[%s:%s]", startPosition, endPosition)

Make a new equals and hashcode functions so the tests can 
actually verify if the moves are the same

