import java.sql.*;
import java.util.Scanner;

public class Main {
    public static Scanner scanner = new Scanner(System.in);
    public static void main(String[] args) {
        displayConsole();
        printGoodbye();
    }

    private static void printGoodbye() {
        System.out.println("Exiting list...");
        System.out.println("Goodbye!");
    }

    private static void displayConsole() {
        boolean on = true;
        while (on) {
            printConsole();
            int input = consoleInput();
            switch (input) {
                case 1 -> showAllMovies();
                case 2 -> showAllGenres();
                case 3 -> showFavorites();
                case 4 -> inputAddMovie();
                case 5 -> inputAddGenre();
                case 6 -> inputDeleteMovie();
                case 7 -> inputDeleteGenre();
                case 0 -> on  = false;
            }
        }
    }

    private static void printConsole() {
        System.out.println("\n Choose: \n");
        System.out.println(
                        "1  - Show all watched movies \n" +
                        "2  - Show genres watched\n" +
                        "3  - Show favorites\n" +
                        "4  - Add a movie\n" +
                        "5  - Add genre\n" +
                        "6  - Delete a movie\n" +
                        "7  - Delete genre\n" +
                        "8  - Search\n" +
                        "0  - Exit"
        );
    }

    private static int consoleInput() {
        int input = 0;
        while (true) {
            try {
                input = Integer.valueOf(scanner.nextLine());
                break;
            } catch (NumberFormatException e) {
                System.out.println("Please choose one of the following options:");
                printConsole();
            }
        }
        return input;
    }

    //Show methods
private static void showAllMovies() {
    String sql = "SELECT movies.movieId, movies.movieTitle," +
                "movies.movieDirector, movies.movieReleaseYear," +
                "movies.movieFavorite, genres.genreName\n" +
                "FROM movies\n" +
                "LEFT JOIN genres\n" +
                "ON genreID = movieGenreId";

    try{
        Connection connection = connect();
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(sql);

        printAllMovies(resultSet);

    } catch (SQLException e){
        System.out.println(e.getMessage());
    }

}

    private static void printAllMovies(ResultSet resultSet) throws SQLException {
        System.out.println();
        while (resultSet.next()) {
            System.out.println(resultSet.getString("movieId") + "\t"
                    + resultSet.getString("movieTitle") + "\t"
                    + resultSet.getString("movieDirector") + "\t"
                    + resultSet.getString("movieReleaseYear") + "\t"
                    + resultSet.getString("movieFavorite") + "\t"
                    + resultSet.getString("genreName"));
        }
    }

    private static void showAllGenres() {
        String sql = "SELECT *FROM genres";

        try{
            Connection connection = connect();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);

            printAllGenres(resultSet);

        } catch (SQLException e){
            System.out.println(e.getMessage());
        }
    }

    private static void printAllGenres(ResultSet resultSet) throws SQLException {
        System.out.println();
        while (resultSet.next()) {
            System.out.println(resultSet.getString("genreId") + "\t"
                    + resultSet.getString("genreName"));
        }
    }

    private static void showFavorites() {

    }

    //Add methods
    private static void inputAddMovie() {
        // Add genre option
        try {
            System.out.println("Enter movie name: ");
            String title = scanner.nextLine();
            System.out.println("Enter movie director: ");
            String director = scanner.nextLine();
            System.out.println("Enter movie year: ");
            int year = Integer.parseInt(scanner.nextLine());
            System.out.println("enter movie genre: ");
            String genre = scanner.nextLine();
            addMovie(title, director, year, genre);
        } catch (Exception e) {
            System.out.println("Woops, something went wrong.");
        }
    }

    private static void addMovie(String title, String director, int year, String genre) {

        String sql = "INSERT INTO movies (movieTitle, movieDirector, movieReleaseYear, movieFavorite, movieGenreId)" +
                " VALUES (?,?,?,'NO',(SELECT genreId FROM genres WHERE genreName = ?))";

        try{
            Connection connection = connect();
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, title);
            preparedStatement.setString(2, director);
            preparedStatement.setInt(3, year);
            preparedStatement.setString(4, genre);
            preparedStatement.executeUpdate();
            System.out.println("The movie was successfully added to the database.");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

    }

    private static void inputAddGenre() {
        System.out.println("Enter genre name: ");
        String genreName = scanner.nextLine();
        addGenre(genreName);
    }

    private static void addGenre(String genreName) {
        String sql = "INSERT INTO genres (genreName) VALUES (?)";
        try{
            Connection connection = connect();
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, genreName);
            preparedStatement.executeUpdate();
            System.out.println("The genre was successfully added to the database.");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    //Delete methods

    private static void inputDeleteMovie() {
        System.out.println("Enter movie ID:");
        int movieId = scanner.nextInt();
        scanner.nextLine();
        deleteMovie(movieId);
    }

    private static void deleteMovie(int movieId) {
        String sql = "DELETE FROM movies WHERE movieId = ?";
        try{
            Connection connection = connect();
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, movieId);
            preparedStatement.executeUpdate();
            System.out.println("The movie has been deleted");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void inputDeleteGenre() {
        System.out.println("Enter genre ID:");
        int genreId = scanner.nextInt();
        scanner.nextLine();
        deleteGenre(genreId);
    }

    private static void deleteGenre(int genreId) {
        String sql = "DELETE FROM genres WHERE genreId = ?";
        try{
            Connection connection = connect();
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, genreId);
            preparedStatement.executeUpdate();
            System.out.println("The genre has been deleted");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }


    private static Connection connect() {
        // SQLite connection string
        String url = "jdbc:sqlite:E:\\SQLiteWatchList\\SQLiteWatchList.db";
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return conn;
    }

}
