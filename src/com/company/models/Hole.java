/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.company.models;

import com.company.helpers.ElementType;

/**
 *
 * @author labredes
 */
public class Hole extends Element {
    
    public Hole(int x, int y) {
        super(x, y, ElementType.hole);
    }
    
}
