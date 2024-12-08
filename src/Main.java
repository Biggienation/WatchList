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
                case 4 -> inputAddMovie();
                case 5 -> inputDeleteMovie();
                case 0 -> on  = false;
            }
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

    private static void inputDeleteMovie() {
        System.out.println("Enter movie ID:");
        int movieId = scanner.nextInt();
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
            String genreIn = scanner.nextLine();
            int genreOut = checkGenre(genreIn);
            addMovie(title, director, year, genreOut);
        } catch (Exception e) {
            System.out.println("Woops, something went wrong.");
        }
    }

    private static int checkGenre(String genreIn) {
        String sql = "SELECT genreId FROM genres WHERE genreName = ?";
        int genreOut = 0;
        try{
            Connection connection = connect();
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, genreIn);
            preparedStatement.executeUpdate();
            ResultSet resultSet = preparedStatement.getResultSet();
            int genreTemp = resultSet.getInt("genreId");

        } catch (SQLException e){
            genreOut = addGenre(genreIn);
        }

        return genreOut;

    }

    private static int addGenre(String genreIn) {
        String sql = "INSERT INTO genres (genreName) VALUES (?)";
        int genreOut = 0;
        try{
            Connection connection = connect();
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, genreIn);
            preparedStatement.executeUpdate();
            ResultSet resultSet = preparedStatement.getResultSet();
            genreOut = resultSet.getInt("genreId");
        } catch (SQLException e){
            System.out.println(e.getMessage());
        }
        return genreOut;
    }

    private static void addMovie(String title, String director, int year, int genre) {

        String sql = "INSERT INTO movies (movieTitle, movieDirector, movieReleaseYear, movieGenreId) VALUES (?,?,?,?)";

        try{
            Connection connection = connect();
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, title);
            preparedStatement.setString(2, director);
            preparedStatement.setInt(3, year);
            preparedStatement.executeUpdate();
            System.out.println("The movie was successfully added to the database.");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

    }

    private static void showAllMovies() {
        String sql = "SELECT *FROM movies";

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
                    + resultSet.getString("movieReleaseYear"));
        }
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

    private static void printConsole() {
        System.out.println("\nChoose:\n");
        System.out.println(
                "1  - Show all watched movies \n" +
                "2  - Show genres watched\n" +
                "3  - Show favorites\n" +
                "4  - Add a movie\n" +
                "5  - Delete a movie\n" +
                "6  - Search\n" +        
                "0  - Exit"
        );
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
