package PUCRS_Estacionamento.TF;

import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.component.page.Meta;
import com.vaadin.flow.server.PWA;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.lumo.Lumo;

/**
 * Configuração global da aplicação Vaadin.
 * Esta classe é necessária para o Vaadin inicializar corretamente o AppShell.
 */
@Theme(themeClass = Lumo.class)
public class AppShell implements AppShellConfigurator {
}
