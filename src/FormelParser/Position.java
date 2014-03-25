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
class Position
{

    /**
     * Creates a new Position class
     * @param value 
     */
    public Position(int value)
    {
        Pos = value;
    }

    /**
     * The current position in an expression string
     */
    public Position()
    {
        this(0);
    }

    public int Pos;

}
