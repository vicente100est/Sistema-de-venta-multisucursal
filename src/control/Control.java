/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package control;

import alertas.ErrorAlert;
import conexion.ConexionBD;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Rojeru San
 */
public class Control {

    //Fichero TMP
    private File archivo = new File("system.tmp");
    //Tiempo en que se actualiza el fichero TMP
    private int segundos = 20;

    public Control() {
    }

    public boolean comprobar() {
        if (archivo.exists()) {
            long tiempo = leer();
            long res = restarTiempo(tiempo);
            if (res < segundos) {
                return false;
            } else {
                programa_tarea();
                return true;
            }
        } else {
            crearTMP();
            programa_tarea();
            return true;
        }
    }

    private long leer() {
        String linea = "0";
        BufferedReader fr;

        try {
            fr = new BufferedReader(new FileReader(archivo));
            while (fr.ready()) {
                linea = fr.readLine();
            }
        } catch (IOException ex) {
            Logger.getLogger(Control.class.getName()).log(Level.SEVERE, null, ex);
        }

        return Long.valueOf(linea).longValue();
    }

    private long restarTiempo(long tiempoActual) {
        Date date = new Date();
        long tiempoTMP = date.getTime();
        long tiempo = tiempoTMP - tiempoActual;
        tiempo = tiempo / 1000;
        return tiempo;
    }

    private void programa_tarea() {
        ScheduledExecutorService sc = Executors.newSingleThreadScheduledExecutor();
        sc.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                crearTMP();
            }
        }, 1000, segundos * 1000, TimeUnit.MILLISECONDS);
    }

    private void crearTMP() {
        Date fecha = new Date();

        try {
            BufferedWriter wr = new BufferedWriter(new FileWriter(archivo));
            wr.write(String.valueOf(fecha.getTime()));
            wr.close();
        } catch (IOException ex) {
            Logger.getLogger(Control.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void cerrarApp() {
        new ConexionBD().desconectar();
        if (archivo.exists()) {
            archivo.delete();
        }
        System.exit(0);
    }
}
