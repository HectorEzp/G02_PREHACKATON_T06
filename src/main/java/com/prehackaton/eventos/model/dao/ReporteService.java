package com.prehackaton.eventos.model.dao;

import com.prehackaton.eventos.model.dao.Conexion;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import javax.swing.SwingUtilities;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.view.JasperViewer;

/**
 * Compila, llena (con la conexión MySQL activa), visualiza y exporta reportes JasperReports.
 * Las consultas SQL viven dentro de los archivos .jrxml (resources/reports).
 */
public class ReporteService {

    public JasperPrint generar(String nombreReporte, Map<String, Object> parametros)
            throws JRException, SQLException {
        try (InputStream in = getClass().getResourceAsStream("/reports/" + nombreReporte + ".jrxml");
             Connection cn = Conexion.getConnection()) {
            if (in == null) {
                throw new JRException("No se encontró el reporte: " + nombreReporte + ".jrxml");
            }
            JasperReport reporte = JasperCompileManager.compileReport(in);
            return JasperFillManager.fillReport(reporte, parametros, cn);
        } catch (IOException e) {
            throw new JRException(e);
        }
    }

    public void verVisor(JasperPrint print, String titulo) {
        SwingUtilities.invokeLater(() -> {
            JasperViewer visor = new JasperViewer(print, false);
            visor.setTitle(titulo);
            visor.setVisible(true);
        });
    }

    public void exportarPdf(JasperPrint print, String rutaArchivo) throws JRException {
        JasperExportManager.exportReportToPdfFile(print, rutaArchivo);
    }
}
