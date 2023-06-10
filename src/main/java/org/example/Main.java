package org.example;

import org.example.props.PropertyReader;
import org.example.socets.Connections;

public class Main {
    public static void main (String[] args) throws Exception {
        new Connections().createConnection(PropertyReader.getConnectionPort());
    }
}