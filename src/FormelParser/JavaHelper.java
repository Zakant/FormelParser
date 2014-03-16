/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package FormelParser;

/**
 *
 * @author cw
 */
class JavaHelper
{

    public static String insert(String s, char c, Position pos)
    {
        StringBuilder sb = new StringBuilder();
        for (int l = 0; l < pos.Pos; l++) // Zeichen links hinzufügen
        {
            sb.append(s.charAt(l));
        }
        sb.append(c); // Zeichen einfügen
        for (int r = pos.Pos; r < s.length(); r++)// Und die Restzeichen rechts einfuegen
        {
            sb.append(s.charAt(r));
        }
        return sb.toString();
    }

    public static int countApperence(String s, char c)
    {
        int returnvalue = 0;
        for (int i = 0; i < s.length(); i++)
        {
            returnvalue += s.charAt(i) == c ? 1 : 0;
        }
        return returnvalue;
    }
}
