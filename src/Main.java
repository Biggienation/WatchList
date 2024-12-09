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
                case 6 -> inputToggleFavorites();
                case 7 -> inputUpdateMovie();
                case 8 -> inputDeleteMovie();
                case 9 -> inputDeleteGenre();
                case 10 -> search();
                case 0 -> on  = false;
            }
        }
    }

    private static void printConsole() {
        printMoviesWatched();
        System.out.println(" Choose:");
        System.out.println(
                """
                        1  - Show all watched movies\s
                        2  - Show genres watched
                        3  - Show favorites
                        4  - Add a movie
                        5  - Add genre
                        6  - Toggle favorites
                        7 -  Update a movie
                        8  - Delete a movie
                        9  - Delete genre
                        10 - Search
                        0  - Exit"""
        );

    }

    private static void printMoviesWatched() {
        String sql = "SELECT Count(*) FROM movies";
        try{
            Connection connection = connect();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);
            while (resultSet.next()) {
                System.out.println("\nWatched movies: " + resultSet.getString(1));
            }
        } catch (SQLException e){
            System.out.println(e.getMessage());
        }
    }

    private static int consoleInput() {
        int input;
        while (true) {
            try {
                input = Integer.parseInt(scanner.nextLine());
                break;
            } catch (NumberFormatException e) {
                System.out.println("Please choose one of the following options:");
                printConsole();
            }
        }
        return input;
    }





    private static void showAllMovies() {
    String sql = "SELECT movies.movieId, movies.movieTitle,movies.movieDirector, movies.movieReleaseYear, movies.movieFavorite, genres.genreName FROM movies JOIN genres ON genreID = movieGenreId";

    try{
        Connection connection = connect();
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(sql);

        printMovies(resultSet);

    } catch (SQLException e){
        System.out.println(e.getMessage());
    }

}
    private static void printMovies(ResultSet resultSet) throws SQLException {
        System.out.println();
        while (resultSet.next()) {
            System.out.println("ID:"
                    + resultSet.getString("movieId") + "\t"
                    + resultSet.getString("movieTitle") + "\t"
                    + resultSet.getString("movieDirector") + "\t"
                    + resultSet.getString("movieReleaseYear") + "\t"
                    + resultSet.getString("genreName") + "\t"
                    + "Favorite: " + resultSet.getString("movieFavorite"));
        }
    }


    private static void showFavorites() {
        String sql = "SELECT movies.movieId, movies.movieTitle," +
                "movies.movieDirector, movies.movieReleaseYear," +
                "movies.movieFavorite, genres.genreName\n" +
                "FROM movies\n" +
                "LEFT JOIN genres\n" +
                "ON genreID = movieGenreId\n" +
                "WHERE movies.movieFavorite = 'YES'";

        try{
            Connection connection = connect();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);

            printMovies(resultSet);

        } catch (SQLException e){
            System.out.println(e.getMessage());
        }
    }
    private static void search() {
        System.out.println("What movie or director do you want to search?");
        String searchQuery = scanner.nextLine();
        String sql = "SELECT movies.movieId, movies.movieTitle," +
                "movies.movieDirector, movies.movieReleaseYear," +
                "movies.movieFavorite, genres.genreName\n" +
                "FROM movies\n" +
                "LEFT JOIN genres\n" +
                "ON genreID = movieGenreId\n" +
                "WHERE movieTitle LIKE '%" + searchQuery + "%' OR movieDirector LIKE '%" + searchQuery + "%'";

        try{
            Connection connection = connect();
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            ResultSet resultSet = preparedStatement.executeQuery();
            printMovies(resultSet);

        } catch (SQLException e){
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
            showAllGenres();
            System.out.println("Enter movie genre ID: ");
            int genre = Integer.parseInt(scanner.nextLine());
            addMovie(title, director, year, genre);
        } catch (Exception e) {
            WhoopsErrorMessage();
        }
    }

    private static void addMovie(String title, String director, int year, int genre) {

        String sql = "INSERT INTO movies (movieTitle, movieDirector, movieReleaseYear, movieFavorite, movieGenreId) VALUES (?,?,?,'NO',?)";

        try{
            Connection connection = connect();
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, title);
            preparedStatement.setString(2, director);
            preparedStatement.setInt(3, year);
            preparedStatement.setInt(4, genre);
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

    private static void inputToggleFavorites() {
        int movieId = getMovieId();
        toggleFavorites(movieId);
    }

    private static void toggleFavorites( int movieId) {
        String sql = "SELECT * FROM movies WHERE movieId = ? ";
        try{
            Connection connection = connect();
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, movieId);
            ResultSet resultSet = preparedStatement.executeQuery();

            String favorite = "";
            while (resultSet.next()) {
                favorite = checkIfFavorite(resultSet);
            }
            updateFavorite(favorite, movieId);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private static String checkIfFavorite(ResultSet resultSet) throws SQLException {
        String favorite = resultSet.getString("movieFavorite");
        if (favorite.equals("YES")) {
            favorite = "NO";
        }else {
            favorite = "YES";
        }
        return favorite;
    }

    private static void updateFavorite(String favorite, int movieId) {
        String sql = "UPDATE movies SET movieFavorite = ? WHERE movieId = ?";
        try {
            Connection connection = connect();
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, favorite);
            preparedStatement.setInt(2, movieId);
            preparedStatement.executeUpdate();
            System.out.println("Toggled favorite to: " + favorite);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void inputUpdateMovie() {
        try {
            int movieId = getMovieId();
            System.out.println("Enter movie name: ");
            String title = scanner.nextLine();
            System.out.println("Enter movie director: ");
            String director = scanner.nextLine();
            System.out.println("Enter movie year: ");
            int year = Integer.parseInt(scanner.nextLine());
            showAllGenres();
            System.out.println("Enter movie genre ID: ");
            int genre = Integer.parseInt(scanner.nextLine());
            updateMovie(movieId, title, director, year, genre);
        } catch (Exception e) {
            WhoopsErrorMessage();
        }
    }

    private static void WhoopsErrorMessage() {
        System.out.println("Whoops, something went wrong.");
    }

    private static void updateMovie(int movieId, String title, String director, int year, int genre) {

        String sql = "UPDATE movies SET movieTitle = ?, movieDirector = ?, movieReleaseYear = ?, movieGenreId = ? WHERE movieId = ?";
        try{
            Connection connection = connect();
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1,title);
            preparedStatement.setString(2,director);
            preparedStatement.setInt(3,year);
            preparedStatement.setInt(4,genre);
            preparedStatement.setInt(5,movieId);
            preparedStatement.executeUpdate();
            System.out.println("The movie was successfully updated.");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void inputDeleteMovie() {
        showAllMovies();
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
            InvalidMovieIdErrorMessage();
        }
    }

    private static void inputDeleteGenre() {
        showAllGenres();
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

    private static void InvalidMovieIdErrorMessage() {
        System.out.println("Please enter a valid movie ID.");
    }

    private static int getMovieId() {
        showAllMovies();
        System.out.println("Enter movie ID: ");
        int movieId = 0;
        try {
            movieId = Integer.parseInt(scanner.nextLine());
        } catch (Exception e) {
            InvalidMovieIdErrorMessage();
        }
        return movieId;
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
            System.out.println("ID: " + resultSet.getString("genreId") + "\t"
                    + resultSet.getString("genreName"));
        }
    }
}
