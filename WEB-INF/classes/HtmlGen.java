// HtmlGen.java
// Class used for offloading the generation of HTML.
//
// Programmer:  Jonathan Godley - c3188072
// Course: SENG2050
// Last modified:  22/03/2018

public class HtmlGen
{
// generate and return doctype string
public static String doctype()
{
        return "<!DOCTYPE html>\n<html lang=\"en\">";
}

// generate and return valid heading and title
// pre: title passed as String
// post: valid html returned as String
public static String head(String title)
{
        return "\t<head>\n\t\t<title>" + title + "</title>\n\t\t<link rel=\"stylesheet\" type=\"text/css\" href=\"css/mystyle.css\">\n\t</head>"; // one line for efficiency
}

// generate and return valid h1 tagged heading
// pre: heading passed as String
// post: valid html returned as String
public static String h1(String heading)
{
        return "<h1>" + heading + "</h1>";
}
}
