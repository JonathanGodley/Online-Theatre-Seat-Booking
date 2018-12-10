// CancellationPage.java
// Class used to cancel a booking by removing it from a text file
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

@WebServlet(urlPatterns = {"/Cancel"})
public class CancellationPage extends HttpServlet
{
public void doGet(HttpServletRequest request, HttpServletResponse response)
throws ServletException, IOException
{
        //HtmlGen generator = new HtmlGen(); // used to quickly generate html tags
        // got a warning about "[static] static method should be qualified by type name, HtmlGen, instead of by an expression"
        // so i switched my generator.doctype() etc to HtmlGen.doctype
        PrintWriter out = response.getWriter();

        String seat = request.getParameter("seat");

        // make sure seat is valid
        if (seat == null || seat == "" || (seat.charAt(0) < 'A' || seat.charAt(0) > 'H') || (seat.charAt(1) < '1' || seat.charAt(1) > '8') ) { response.sendRedirect("Home"); }
        else
        {
                String tmpbooking;
                boolean notBooked = false;

                try
                {
                        // read database
                        InputStream input = getServletContext().getResourceAsStream("/WEB-INF/a1data.txt");
                        BufferedReader b = new BufferedReader(new InputStreamReader(input, "UTF-8"));

                        // read our database file into the program
                        for (int i = 0; i < 64; i++)
                        {
                                tmpbooking = b.readLine();
                                if (tmpbooking.substring(0, 2).equals(seat.substring(0, 2)) && tmpbooking.length() < 3)
                                {
                                        notBooked = true;
                                }
                        }
                        b.close();
                }
                catch (IOException e)
                {
                        e.printStackTrace();
                        out.println("\t\tError Accessing Database");
                }

                if (notBooked) // if seat not already booked
                {
                        //output some crap to the browser
                        out.println(HtmlGen.doctype());
                        out.println("\t<head>\n\t\t<title>" + "Cancellation Failed" + "</title>\n\t\t<link rel=\"stylesheet\" type=\"text/css\" href=\"css/mystyle.css\">");
                        out.println("\t\t<meta http-equiv=\"Refresh\" content=\"20; url=Home\">");
                        out.println("\t</head>");

                        out.println("\t<body>");
                        out.println("\t\t" + HtmlGen.h1("Cancellation Failed"));
                        out.println("\t\t<p>This booking either does not exist or has already been cancelled</p>");
                        out.println("\t\t<p>Redirecting to homepage in 20 seconds</p>");
                        // introduced a 20 second delay so user can read error message

                        out.println("\t</body>");
                        out.println("</html>");
                        out.close();
                }
                else
                {
                        out.println(HtmlGen.doctype());
                        out.println(HtmlGen.head("Cancel a Booking"));

                        out.println("\t<body>");
                        out.println("\t\t" + HtmlGen.h1("Cancel a booking for seat <span id=\"seat\">" + seat + "</span>"));

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

                        String[] booking = new String[5]; //SEAT|USERID|PHONE|ADDRESS|EMAIL

                        // now we need to pull the information for the booking so that we have somthing to compare.
                        try
                        {
                                InputStream input = getServletContext().getResourceAsStream("/WEB-INF/a1data.txt");
                                BufferedReader b = new BufferedReader(new InputStreamReader(input, "UTF-8"));


                                // find the booking in question
                                for (int i = 0; i < 64; i++)
                                {
                                        tmpbooking = b.readLine();
                                        if (tmpbooking.substring(0, 2).equals(seat.substring(0, 2)))
                                        {
                                                booking = tmpbooking.split("\\|");
                                        }
                                }
                                b.close();

                        }
                        catch (IOException e)
                        {
                                e.printStackTrace();
                                out.println("\t\tError Accessing Database");
                        }

                        // our form with validation
                        out.println("\t\t<script src=\"js/validateCancel.js\"></script>");
                        out.println(
                                "\t\t<form action=\"Cancel\" method=\"post\" onsubmit=\"return validateCancel();\">" +

                                "\n\t\t\t<label for=\"userID\">UserID*</label>" +
                                "\n\t\t\t<input type=\"text\" name=\"userID\" id=\"userID\" /><br>");

                        // need to choose a field to display, need to make sure it's available as well.
                        // SEAT|USERID|PHONE|ADDRESS|EMAIL


                        int method = 0;
                        if (booking[2] != null && booking[3] != null && !booking[2].equals("") && !booking[3].equals("")) // all fields provided
                        {
                                method = random.nextInt(3 - 1 + 1) + 1;
                        }
                        else if ((booking[2] == null || booking[2].equals("")) && (booking[3] == null || booking[3].equals(""))) // no phone or address
                        {
                                method = 3;
                        }
                        else if (booking[2] == null || booking[2].equals("")) // no phone
                        {
                                method = random.nextInt(3 - 2 + 1) + 2;
                        }
                        else //if (booking[3] == null || booking[3].equals("")) // no address
                        {
                                method = random.nextInt(3 - 2 + 1) + 2;
                                if (method == 2) { method = 1; } // can't really do a random number between 1 & 3, but not 2, so i've just done 2 or 3, and made 2 equal 1
                        }


                        switch (method)
                        {
                        case 1:
                                out.println("\t\t\t<label for=\"verification\">Phone</label>" +
                                            "\n\t\t\t<input type=\"text\" name=\"phone\" id=\"verification\" /><br>");
                                break;
                        case 2:
                                out.println("\n\t\t\t<label for=\"verification\">Address</label>" +
                                            "\n\t\t\t<input type=\"text\" name=\"address\" id=\"verification\" /><br>");
                                break;
                        case 3:
                                out.println("\n\t\t\t<label for=\"verification\">Email*</label>" +
                                            "\n\t\t\t<input type=\"text\" name=\"email\" id=\"verification\" /><br>");
                                break;
                        }

                        out.println("\t\t\t<label for=\"securityCode\">Security Code*</label>" +
                                    "\n\t\t\t<input type=\"text\" name=\"securityCode\" id=\"securityCode\" /><br>" +

                                    "\n\t\t\t<INPUT TYPE=\"hidden\" NAME=\"seat\" VALUE=\"" + seat + "\">" + // hidden field to pass along our seat number in the post request
                                    "\n\t\t\t<INPUT TYPE=\"hidden\" NAME=\"method\" VALUE=\"" + method + "\">" + // hidden field to pass along which method of verification was used.

                                    "\n\t\t\t<input type=\"reset\" value=\"Clear\" />" +
                                    "\n\t\t\t<input type=\"submit\" value=\"Cancel the Booking\" />" +

                                    "\n\t\t</form>");

                        out.println("\t\t<p>* - Required</p>");
                        out.println("\t\t<p>Note: UserID must not contain numbers</p>");

                        out.println("\t</body>");
                        out.println("</html>");
                        out.close();
                }
        }

}

public void doPost(HttpServletRequest request, HttpServletResponse response)
throws ServletException, IOException
{
        PrintWriter out = response.getWriter();

        String seat = request.getParameter("seat");
        String userID = request.getParameter("userID");
        int method = Integer.parseInt(request.getParameter("method"));
        String phone = "", address = "", email = "";

        // determine which field we're checking against
        if (method == 1) { phone = request.getParameter("phone"); }
        else if (method == 2) { address = request.getParameter("address"); }
        else { email = request.getParameter("email"); }

        // check if information matches
        String[] booking = new String[5]; //SEAT|USERID|PHONE|ADDRESS|EMAIL

        // now we need to pull the information for the booking so that we have somthing to compare.
        try
        {
                InputStream input = getServletContext().getResourceAsStream("/WEB-INF/a1data.txt");
                BufferedReader b = new BufferedReader(new InputStreamReader(input, "UTF-8"));

                // find the booking in question
                for (int i = 0; i < 64; i++)
                {
                        String tmpbooking = b.readLine();
                        if (tmpbooking.substring(0, 2).equals(seat.substring(0, 2)))
                        {
                                booking = tmpbooking.split("\\|");
                        }
                }
                b.close();

        }
        catch (IOException e)
        {
                e.printStackTrace();
                out.println("\t\tError Accessing Database");
        }

        // convert our method into the appropriate index to access the correct information
        // in our booking array
        int methodIndex = 0;
        if (method == 1) { methodIndex = 2; }
        else if (method == 2) { methodIndex = 3; }
        else { methodIndex = 4; }

        String tmpbooking;
        boolean notBooked = false;

        try
        {
                InputStream input = getServletContext().getResourceAsStream("/WEB-INF/a1data.txt");
                BufferedReader b = new BufferedReader(new InputStreamReader(input, "UTF-8"));

                // read our initial database file into the program
                for (int i = 0; i < 64; i++)
                {
                        tmpbooking = b.readLine();
                        if (tmpbooking.substring(0, 2).equals(seat.substring(0, 2)) && tmpbooking.length() < 3)
                        {
                                notBooked = true;
                        }
                }
                b.close();
        }
        catch (IOException e)
        {
                e.printStackTrace();
                out.println("\t\tError Accessing Database");
        }



        if (notBooked)
        {
                //output some crap to the browser
                out.println(HtmlGen.doctype());
                out.println("\t<head>\n\t\t<title>" + "Cancellation Failed" + "</title>\n\t\t<link rel=\"stylesheet\" type=\"text/css\" href=\"css/mystyle.css\">");
                out.println("\t\t<meta http-equiv=\"Refresh\" content=\"20; url=Home\">");
                out.println("\t</head>");

                out.println("\t<body>");
                out.println("\t\t" + HtmlGen.h1("Cancellation Failed"));
                out.println("\t\t<p>This booking either does not exist or has already been cancelled</p>");
                out.println("\t\t<p>Redirecting to homepage in 20 seconds</p>");
                // introduced a 20 second delay so user can read error message

                out.println("\t</body>");
                out.println("</html>");
                out.close();

        }
        else if (((booking[methodIndex].equals(phone)) && phone != null) || ((booking[methodIndex].equals(address)) && address != null) || ((booking[methodIndex].equals(email)) && email != null))
        {
                // we have a match, delete the entry
                // array to hold our database temporarily
                String[] bookings = new String[64];

                try
                {
                        InputStream input = getServletContext().getResourceAsStream("/WEB-INF/a1data.txt");
                        BufferedReader b = new BufferedReader(new InputStreamReader(input, "UTF-8"));
                        String line = "";

                        // read our initial database file into the program
                        for (int i = 0; i < 64; i++)
                        {
                                bookings[i] = b.readLine();
                        }
                        b.close();


                        // overwrite our database file
                        BufferedWriter writer = new BufferedWriter(new FileWriter(new File(getServletContext().getRealPath("/WEB-INF/") + "a1data.txt")));
                        for (int i = 0; i < 64; i++)
                        {
                                if (bookings[i].substring(0, 2).equals(seat.substring(0, 2))) // if selected seat and currently being written seat match
                                {
                                        writer.write(seat);
                                        writer.write("\n");
                                }
                                else
                                {
                                        writer.write(bookings[i]);
                                        writer.write("\n");
                                }
                        }
                        writer.close();
                }
                catch (IOException e)
                {
                        e.printStackTrace();
                        out.println("\t\tError Accessing Database");
                }

                //output some crap to the browser
                out.println(HtmlGen.doctype());
                out.println("\t<head>\n\t\t<title>" + "Booking Cancelled" + "</title>\n\t\t<link rel=\"stylesheet\" type=\"text/css\" href=\"css/mystyle.css\">");
                out.println("\t\t<meta http-equiv=\"Refresh\" content=\"5; url=Home\">");
                out.println("\t</head>");

                out.println("\t<body>");
                out.println("\t\t" + HtmlGen.h1("Booking Successfully Cancelled!"));
                out.println("\t\t<p>Redirecting to homepage in 5 seconds</p>");
                // introduced a 5 second delay instead of an instant redirect to allow the BufferedWriter to finish flushing
                // without the delay the seat-grid wouldn't show the new booking until another refresh

                out.println("\t</body>");
                out.println("</html>");
                out.close();

        }
        else
        {
                //output some crap to the browser
                out.println(HtmlGen.doctype());
                out.println("\t<head>\n\t\t<title>" + "Cancellation Failed" + "</title>\n\t\t<link rel=\"stylesheet\" type=\"text/css\" href=\"css/mystyle.css\">");
                out.println("\t\t<meta http-equiv=\"Refresh\" content=\"20; url=Home\">");
                out.println("\t</head>");

                out.println("\t<body>");
                out.println("\t\t" + HtmlGen.h1("Cancellation Failed"));
                out.println("\t\t<p>The provided details did not match the booking information</p>");
                out.println("\t\t<p>Redirecting to homepage in 20 seconds</p>");
                // introduced a 20 second delay so user can read error message

                out.println("\t</body>");
                out.println("</html>");
                out.close();
        }
}
}
