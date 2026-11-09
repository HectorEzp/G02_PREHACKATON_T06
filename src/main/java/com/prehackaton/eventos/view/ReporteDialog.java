package com.prehackaton.eventos.view;

import com.prehackaton.eventos.model.dao.ReporteService;
import java.io.File;
import java.util.Map;
import java.util.Optional;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.stage.FileChooser;
import net.sf.jasperreports.engine.JasperPrint;

/** Flujo de UI común: generar un reporte y elegir entre JasperViewer o exportación a PDF. */
public final class ReporteDialog {

    private ReporteDialog() {
    }

    public static void mostrar(String titulo, String recurso, Map<String, Object> parametros) {
        ReporteService servicio = new ReporteService();
        JasperPrint print;
        try {
            print = servicio.generar(recurso, parametros);
        } catch (Exception ex) {
            Alertas.error("No se pudo generar el reporte", ex);
            return;
        }
        if (print.getPages().isEmpty()) {
            Alertas.info("Sin datos", "El reporte no contiene registros para los filtros elegidos.");
            return;
        }

        ButtonType ver = new ButtonType("Ver en JasperViewer");
        ButtonType pdf = new ButtonType("Exportar PDF");
        ButtonType cerrar = new ButtonType("Cerrar", ButtonBar.ButtonData.CANCEL_CLOSE);
        Alert dialogo = new Alert(Alert.AlertType.CONFIRMATION,
                "El reporte se generó correctamente con " + print.getPages().size() + " página(s).\n¿Qué desea hacer?",
                ver, pdf, cerrar);
        dialogo.setTitle(titulo);
        dialogo.setHeaderText(titulo);

        Optional<ButtonType> opcion = dialogo.showAndWait();
        if (opcion.isEmpty()) {
            return;
        }
        if (opcion.get() == ver) {
            servicio.verVisor(print, titulo);
        } else if (opcion.get() == pdf) {
            FileChooser fc = new FileChooser();
            fc.setTitle("Guardar reporte PDF");
            fc.setInitialFileName(recurso + ".pdf");
            fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Archivo PDF", "*.pdf"));
            File destino = fc.showSaveDialog(null);
            if (destino != null) {
                try {
                    servicio.exportarPdf(print, destino.getAbsolutePath());
                    Alertas.info("Reporte exportado", "Archivo guardado en:\n" + destino.getAbsolutePath());
                } catch (Exception ex) {
                    Alertas.error("No se pudo exportar el PDF", ex);
                }
            }
        }
    }
}
