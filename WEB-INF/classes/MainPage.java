// MainPage.java
// Class used to generate dynamic homepage of an online booking system.
//
// Programmer:  Jonathan Godley - c3188072
// Course: SENG2050
// Last modified:  25/03/2018
import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.WebServlet;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

@WebServlet(urlPatterns = {"/Home"})
public class MainPage extends HttpServlet
{
public void doGet(HttpServletRequest request, HttpServletResponse response)
throws ServletException, IOException
{
        //HtmlGen generator = new HtmlGen(); // used to quickly generate html tags
        // got a warning about "[static] static method should be qualified by type name, HtmlGen, instead of by an expression"
        // so i switched my generator.doctype() etc to HtmlGen.doctype
        PrintWriter out = response.getWriter();

        out.println(HtmlGen.doctype());
        out.println(HtmlGen.head("Sunrise Theatre Online Booking Portal"));
        out.println("\t<body>");

        // appropriate heading
        out.println("\t\t" + HtmlGen.h1("Sunrise Theatre Online Booking Portal"));

        // generate dynamic table based on available seating using a text file database and looping.
        // open our text file
        try
        {

                // file format = SEAT|USERID|PHONE|ADDRESS|EMAIL each line
                InputStream input = getServletContext().getResourceAsStream("/WEB-INF/a1data.txt");
                BufferedReader b = new BufferedReader(new InputStreamReader(input, "UTF-8"));
                String line = "";

                // start outputting our table
                out.println("\t\t<table>");
                out.println("\t\t\t<thead>");
                out.println("\t\t\t\t<tr>");
                out.println("\t\t\t\t\t<th></th>"); // empty space above the A, top left corner space

                // loop to create the table header
                for (int i = 1; i < 9; i++)
                {
                        out.println("\t\t\t\t\t<th>"+ i +"</th>");
                }
                out.println("\t\t\t\t</tr>");
                out.println("\t\t\t</thead>");

                out.println("\t\t\t<tbody>");
                // loop to generate the rows
                for (char letter = 'A'; letter < 'I'; letter++)
                {
                        out.println("\t\t\t\t<tr>");
                        out.println("\t\t\t\t\t<th>" + letter + "</th>");
                        // now loop along the row to fill the numbers
                        for (int i = 1; i < 9; i++)
                        {
                                line = b.readLine();
                                // the magic happens here, we have our line string, which contains our SEAT|USERID|PHONE|ADDRESS|EMAIL
                                if (line.length() <= 3)
                                {
                                        // seat available
                                        out.println("\t\t\t\t\t<td class=\"available\">" + "<a href=\"Book?seat=" + letter + i + "\">" + letter + i + "</a></td>");
                                }
                                else
                                {
                                        // seat not available
                                        out.println("\t\t\t\t\t<td class=\"occupied\">" + "<a href=\"Cancel?seat=" + letter + i + "\">" + letter + i + "</a></td>");
                                }
                        }
                        out.println("\t\t\t\t</tr>");
                }
                out.println("\t\t\t</tbody>");
                out.println("\t\t</table>");
                b.close();
        }
        catch (IOException e)
        {
                e.printStackTrace();
                out.println("\t\tError Accessing Database");
        }

        // current date & time, formatted to DD-MM-YY SS-MM-HH
        SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd-MM-yy ss:mm:HH");
        Date dt = new Date();
        String now = DATE_FORMAT.format(dt);
        out.println("\t\t<p>Availabilities accurate as of " + now + ", refresh to update available seats.</p>");

        out.println("\t</body>");
        out.println("</html>");
        out.close();

}
}
