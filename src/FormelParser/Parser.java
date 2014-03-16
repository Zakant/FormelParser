/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package FormelParser;

import java.util.*;

/**
 *
 * @author cw
 */
public class Parser
{

    /**
     * Parses an expressionstring and return its value
     * @param s
     * @return The numeric value of the string
     * @throws ArgumentException
     * @throws InvalidOperationException 
     */
    public static Double ParsString(String s) throws ArgumentException, InvalidOperationException
    {
        Position p = new Position();
        String finalstring = prepareString(s, true);
        System.out.println("DEBUG: " + finalstring);
        return Pars(finalstring, p);
    }

    private static final char[] ops =
    {
        '*', '/', '\\', '%', '^'
    };

    private static Double Pars(String s, Position pos) throws ArgumentException
    {
        boolean cancle = false;
        boolean executeop = false;
        boolean handeled = false;
        Stack<Double> stack = new Stack<>();
        char op = (char) 0;
        double v1;
        double v2;

        while (!cancle)
        {
            char c = s.charAt(pos.Pos);
            if (Arrays.asList(ops).contains(c))
            {
            } else
            {
                if (c == '(')
                {
                    pos.Pos++;
                    stack.push(Pars(s, pos));
                    pos.Pos++;
                    if (op != 0)
                    {
                        executeop = true;
                    }
                    handeled = true;
                } else if (c == '+' || c == '-' || Character.isDigit(c))
                {

                    stack.push(getNumber(s, pos));
                    if (op != 0)
                    {
                        executeop = true;
                    }
                    handeled = true;
                } else if (c == ')')
                {
                    cancle = true;
                    handeled = true;
                } else
                {
                    if (op != 0)
                    {
                        throw new ArgumentException("Operator Exception");
                    }
                    op = c;
                    pos.Pos++;
                    handeled = true;
                }
            }
            if (executeop)
            {
                executeop = false;
                switch (op)
                {

                    case '*':
                        stack.push(stack.pop() * stack.pop());
                        break;
                    case '\\':
                        v1 = stack.pop();
                        v2 = stack.pop();
                        stack.push((double) (int) (v2 / v1));
                        break;
                    case '/':
                        v1 = stack.pop();
                        v2 = stack.pop();
                        stack.push(v2 / v1);
                        break;
                    case '%':
                        v1 = stack.pop();
                        v2 = stack.pop();
                        stack.push(v2 % v1);
                        break;
                    case '^':
                        v1 = stack.pop();
                        v2 = stack.pop();
                        stack.push(Math.pow(v2, v1));
                        break;
                }
                op = (char) 0;
            }
            if (pos.Pos >= s.length())
            {
                cancle = true;
            }
            if (handeled == false)
            {
                throw new ArgumentException("'" + c + "' is not a vaild character!");
            } else
            {
                handeled = false;
            }
        }

        return CalcStack(stack);
    }

    private static Double CalcStack(Stack<Double> stack)
    {
        double result = 0;
        while (stack.size() > 0)
        {
            result += stack.pop();
        }
        return result;
    }

    private static Double getNumber(String s, Position pos) throws ArgumentException
    {
        boolean isNegative = false;
        String outstring = "";
        char c = s.charAt(pos.Pos);

        if (c == '-')
        {
            isNegative = true;
            pos.Pos++;
        } else if (c == '+')
        {
            isNegative = false;
            pos.Pos++;
        }

        while (pos.Pos < s.length() && (Character.isDigit(s.charAt(pos.Pos)) || s.charAt(pos.Pos) == '.'))
        {
            outstring += s.charAt(pos.Pos);
            pos.Pos++;
        }
        double temp = 0;
        if (pos.Pos < s.length() && s.charAt(pos.Pos) == '(')
        {
            pos.Pos++; //Auf das Zeichen nach der Klamer springen
            temp = Pars(s, pos); //Die Klammer parsen
            pos.Pos++; //Hinter die schließende Klammer springen
            return temp * (isNegative ? -1 : 1);
        }

        return Double.parseDouble(outstring) * (isNegative ? -1 : 1);

    }

    private static String prepareString(String s, boolean addbrackets) throws InvalidOperationException
    {
        s = s.replace(" ", "").replace(",", ".");
        s = s.toLowerCase();
        int bauf = JavaHelper.countApperence(s, '('); //Original: LINQ
        int bzu = JavaHelper.countApperence(s, ')'); //Original: LINQ

        if (bauf != bzu)
        {
            if (bauf > bzu)
            {
                int diff = bauf - bzu;
                if (addbrackets)
                {
                    for (int i = 0; i < diff; i++)
                    {
                        s += ')';
                    }
                } else
                {
                    throw new InvalidOperationException(diff + " brackets aren't closed corectley");
                }
            } else
            {
                int diff = bzu - bauf;
                if (addbrackets)
                {
                    for (int i = 0; i < diff; i++)
                    {
                        s = ')' + s;
                    }
                } else
                {
                    throw new InvalidOperationException(diff + " brackets aren't opened corectley");
                }
            }

        }
        //Durchgehen von Allen öffnen Klammern und das Zeichen links daneben checken
        int mainposb = 0;
        while (true)
        {
            Position pos = new Position(s.indexOf('(', mainposb));
            if (pos.Pos < 0 || pos.Pos - 1 < 0)
            {
                break;
            }
            if (Character.isDigit(s.charAt(pos.Pos - 1)))
            {
                s = JavaHelper.insert(s, '*', pos);
                pos.Pos++;
            }

            mainposb = pos.Pos + 1;
            if (mainposb > s.length())
            {
                break;
            }
        }

        //Anpassen von Klammern um die Potenzen
        int mainpos = 0;

        while (true)
        {
            int pos = s.indexOf('^', mainpos);
            if (pos == -1)
            {
                break;
            }

            Position left = new Position();
            Position right = new Position();

            getLeftandRight(s, pos, left, right);

            s = JavaHelper.insert(s, ')', right);
            s = JavaHelper.insert(s, '(', left);

            mainpos = pos + 2;

            if (mainpos > s.length())
            {
                break;
            }
        }
        return s;
    }

    private static void getLeftandRight(String s, int pos, Position left, Position right)
    {
        //char[] letters = "abcdefghijklmnopqrstuvwxyz".toCharArray();

        //Nach links
        left.Pos = pos - 1;
        if (s.charAt(left.Pos) == ')')
        {
            int brakets = 1;
            left.Pos--;
            while (left.Pos > 0 && brakets != 0)
            {
                if (s.charAt(left.Pos) == ')')
                {
                    brakets++;
                }
                if (s.charAt(left.Pos) == '(')
                {
                    brakets--;
                }
                left.Pos--;
            }
        } else
        {
            while (left.Pos > 0 && (Character.isDigit(s.charAt(left.Pos)) || s.charAt(left.Pos) == '+' || s.charAt(left.Pos) == '-'))
            {
                left.Pos--;
            }
            left.Pos += left.Pos > 0x0 ? 0x1 : 0x0;
        }

        //Nach Rechts
        right.Pos = pos + 1;
        if (s.charAt(right.Pos) == '(')
        {
            int brakets = 1;
            right.Pos++;
            while (right.Pos < s.length() && brakets != 0)
            {
                if (s.charAt(right.Pos) == '(')
                {
                    brakets++;
                }
                if (s.charAt(right.Pos) == ')')
                {
                    brakets--;
                }
                right.Pos++;
            }
        } else
        {
            while (right.Pos < s.length() && (Character.isDigit(s.charAt(right.Pos)) || s.charAt(right.Pos) == '+' || s.charAt(right.Pos) == '-'))
            {
                right.Pos++;
            }
        }

    }

}
