// BookingPage.java
// Class used to create a booking by writing to a text file
//
// Programmer:  Jonathan Godley - c3188072
// Course: SENG2050
// Last modified:  26/03/2018
import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.WebServlet;
import java.util.Random;
import java.io.BufferedWriter;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

@WebServlet(urlPatterns = {"/Book"})
public class BookingPage extends HttpServlet
{
public void doGet(HttpServletRequest request, HttpServletResponse response)
throws ServletException, IOException
{
        //HtmlGen generator = new HtmlGen(); // used to quickly generate html tags
        // got a warning about "[static] static method should be qualified by type name, HtmlGen, instead of by an expression"
        // so i switched my generator.doctype() etc to HtmlGen.doctype
        PrintWriter out = response.getWriter();

        String seat = request.getParameter("seat");

        // make sure this is a valid seat, that is not already booked.
        // note, a user can manually open the booking page for a taken seat, by editing the URL,
        //  however, when they hit submit, they will be told it's already booked by the doPost method.
        if (seat == null || seat == "" || (seat.charAt(0) < 'A' || seat.charAt(0) > 'H') || (seat.charAt(1) < '1' || seat.charAt(1) > '8') ) { response.sendRedirect("Home"); }
        else
        {
                out.println(HtmlGen.doctype());
                out.println(HtmlGen.head("Create a Booking"));

                out.println("\t<body>");
                out.println("\t\t" + HtmlGen.h1("Create a booking for seat <span id=\"seat\">" + seat + "</span>"));

                // our auto-generated security code
                // 6 characters, numbers and letters.
                String code = "";
                String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
                String integers = "0123456789";

                Random random = new Random(); // init a new instance of random class to help us pick a random character from our allowed characters

                for (int i = 0; i < 3; i++)
                {
                        // extremely quick and dirty way to get a mixed string to use as a security code
                        // if it wasn't a one time use code i'd go to much more effort
                        // I've ensured that every code will have an even mixture of numbers and letters
                        code += alphabet.charAt(random.nextInt(alphabet.length()));
                        code += integers.charAt(random.nextInt(integers.length()));
                }

                out.println("\t\t<p>Security Code: <span id=\"code\">" + code + "</span></p>");

                // our form with validation
                out.println("\t\t<script src=\"js/validateBooking.js\"></script>");
                out.println(
                        "\t\t<form action=\"Book\" method=\"post\" onsubmit=\"return validateBooking();\">" +

                        "\n\t\t\t<label for=\"userID\">UserID*</label>" +
                        "\n\t\t\t<input type=\"text\" name=\"userID\" id=\"userID\" /><br>" +

                        "\n\t\t\t<label for=\"phone\">Phone</label>" +
                        "\n\t\t\t<input type=\"text\" name=\"phone\" id=\"phone\" /><br>" +

                        "\n\t\t\t<label for=\"address\">Address</label>" +
                        "\n\t\t\t<input type=\"text\" name=\"address\" id=\"address\" /><br>" +

                        "\n\t\t\t<label for=\"email\">Email*</label>" +
                        "\n\t\t\t<input type=\"text\" name=\"email\" id=\"email\" /><br>" +

                        "\n\t\t\t<label for=\"securityCode\">Security Code*</label>" +
                        "\n\t\t\t<input type=\"text\" name=\"securityCode\" id=\"securityCode\" /><br>" +

                        "\n\t\t\t<INPUT TYPE=\"hidden\" NAME=\"seat\" VALUE=\"" + seat + "\">" + // hidden field to pass along our seat number in the post request

                        "\n\t\t\t<input type=\"reset\" value=\"Clear\" />" +
                        "\n\t\t\t<input type=\"submit\" value=\"Submit\" />" +

                        "\n\t\t</form>");

                out.println("\t\t<p>* - Required</p>");
                out.println("\t\t<p>Note: UserID must not contain numbers</p>");

                out.println("\t</body>");
                out.println("</html>");
                out.close();
        }

}

public void doPost(HttpServletRequest request, HttpServletResponse response)
throws ServletException, IOException
{
        PrintWriter out = response.getWriter();
        String seat = request.getParameter("seat");
        String userID = request.getParameter("userID");
        String address = request.getParameter("address");
        String phone = request.getParameter("phone");
        String email = request.getParameter("email");

        // format our input string for database - SEAT|USERID|PHONE|ADDRESS|EMAIL
        String booking = seat + "|" + userID + "|" + phone + "|" + address + "|" + email;

        // array to hold our database temporarily
        String[] bookings = new String[64];
        int userIDCount = 0;
        boolean alreadyBooked = false;

        try
        {
                InputStream input = getServletContext().getResourceAsStream("/WEB-INF/a1data.txt");
                BufferedReader b = new BufferedReader(new InputStreamReader(input, "UTF-8"));
                String[] tmpbooking = new String[5]; //SEAT|USERID|PHONE|ADDRESS|EMAIL

                // read our initial database file into the program
                for (int i = 0; i < 64; i++)
                {
                        bookings[i] = b.readLine();
                        tmpbooking = bookings[i].split("\\|");
                        if (bookings[i].substring(0, 2).equals(seat.substring(0, 2)) && bookings[i].length() > 3)
                        {
                                alreadyBooked = true;
                        }
                        if (bookings[i].length() > 3 && tmpbooking[1].equals(userID))
                        {
                                userIDCount++; // whenever a booking is made by the current userID, increment
                        }
                }
                b.close();

                if (alreadyBooked) // if seat already booked
                {
                        //output some crap to the browser
                        out.println(HtmlGen.doctype());
                        out.println("\t<head>\n\t\t<title>" + "Booking Cancelled" + "</title>\n\t\t<link rel=\"stylesheet\" type=\"text/css\" href=\"css/mystyle.css\">");
                        out.println("\t\t<meta http-equiv=\"Refresh\" content=\"5; url=Home\">");
                        out.println("\t</head>");

                        out.println("\t<body>");
                        out.println("\t\t" + HtmlGen.h1("Booking Cancelled"));
                        out.println("\t\t<p>This seat has already been booked</p>");
                        out.println("\t\t<p>Redirecting to homepage in 20 seconds</p>");
                        // introduced a 5 second delay instead of an instant redirect to allow the BufferedWriter to finish flushing
                        // without the delay the seat-grid wouldn't show the new booking until another refresh

                        out.println("\t</body>");
                        out.println("</html>");
                        out.close();
                }
                else if (userIDCount < 3)
                {

                        // overwrite our database file
                        BufferedWriter writer = new BufferedWriter(new FileWriter(new File(getServletContext().getRealPath("/WEB-INF/") + "a1data.txt")));
                        for (int i = 0; i < 64; i++)
                        {
                                if (bookings[i].substring(0, 2).equals(seat.substring(0, 2))) // if selected seat and currently being written seat match
                                {
                                        writer.write(booking);
                                        writer.write("\n");
                                }
                                else
                                {
                                        writer.write(bookings[i]);
                                        writer.write("\n");
                                }
                        }
                        writer.close();

                        //output some crap to the browser
                        out.println(HtmlGen.doctype());
                        out.println("\t<head>\n\t\t<title>" + "Booking Created" + "</title>\n\t\t<link rel=\"stylesheet\" type=\"text/css\" href=\"css/mystyle.css\">");
                        out.println("\t\t<meta http-equiv=\"Refresh\" content=\"5; url=Home\">");
                        out.println("\t</head>");

                        out.println("\t<body>");
                        out.println("\t\t" + HtmlGen.h1("Booking Successfully Created!"));
                        out.println("\t\t<p>Redirecting to homepage in 5 seconds</p>");
                        // introduced a 5 second delay instead of an instant redirect to allow the BufferedWriter to finish flushing
                        // without the delay the seat-grid wouldn't show the new booking until another refresh

                        out.println("\t</body>");
                        out.println("</html>");
                        out.close();
                }
                else // too many bookings for userid
                {
                        //output some crap to the browser
                        out.println(HtmlGen.doctype());
                        out.println("\t<head>\n\t\t<title>" + "Booking Cancelled" + "</title>\n\t\t<link rel=\"stylesheet\" type=\"text/css\" href=\"css/mystyle.css\">");
                        out.println("\t\t<meta http-equiv=\"Refresh\" content=\"5; url=Home\">");
                        out.println("\t</head>");

                        out.println("\t<body>");
                        out.println("\t\t" + HtmlGen.h1("Booking Cancelled"));
                        out.println("\t\t<p>There are already 3 bookings assosciated with this UserID</p>");
                        out.println("\t\t<p>Redirecting to homepage in 20 seconds</p>");
                        // introduced a 5 second delay instead of an instant redirect to allow the BufferedWriter to finish flushing
                        // without the delay the seat-grid wouldn't show the new booking until another refresh

                        out.println("\t</body>");
                        out.println("</html>");
                        out.close();
                }
        }
        catch (IOException e)
        {
                e.printStackTrace();
                out.println("\t\tError Accessing Database");
        }
}
}
