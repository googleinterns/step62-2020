package com.google.sps.servlets;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.google.sps.Account;
import com.google.sps.ServletLibray;
import com.google.gson.Gson;

// This servlet retrieves the account information of the user and sends it as
// a response.
@WebServlet("/login")
public class AccountServlet extends HttpServlet {

  protected DatastoreService datastore;
  protected UserService userService;
  protected Gson gson;

  public AccountServlet() {
    super();
    datastore = DatastoreServiceFactory.getDatastoreService();
    userService = UserServiceFactory.getUserService();
    gson = new Gson();
  }

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

    // First check if the user is logged in, otherwise redirect to google login page.
    if (userService.isUserLoggedIn()) {
      // Next check if the account is registered in the database, otherwise go to account creation.
      Filter filter = new FilterPredicate("userId", FilterOperator.EQUAL, userService.getCurrentUser().getUserId());
      Query query = new Query("userId").setFilter(filter);
      PreparedQuery pq = datastore.prepare(query);
      Entity result = pq.asSingleEntity();
      if (result != null) {
        // Retrieve all account information.
        Account account = ServletLibray.retrieveAccountInfo(result);
        String json = gson.toJson(account);
        response.setContentType("application/json;");
        response.getWriter().println(json);
      } else {
        response.sendRedirect("/chooseAccount.html");
      }
    } else {
      String loginUrl = userService.createLoginURL("/login");
      response.sendRedirect(loginUrl);
    }
  }

}