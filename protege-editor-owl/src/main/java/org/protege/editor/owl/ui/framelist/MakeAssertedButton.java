package org.protege.editor.owl.ui.framelist;

import org.protege.editor.core.ui.list.MListButton;

import java.awt.*;
import java.awt.event.ActionListener;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 01-Mar-2007<br><br>
 */
public class MakeAssertedButton extends MListButton {

    public MakeAssertedButton(ActionListener actionListener) {
        super("Convert inference to assertion", new Color(100, 40, 140), actionListener);
    }


    public void paintButtonContent(Graphics2D g) {
        int stringWidth = g.getFontMetrics().getStringBounds("A", g).getBounds().width;
        int w = getBounds().width;
        int h = getBounds().height;
        g.drawString("A",
                     getBounds().x + w / 2 - stringWidth / 2,
                     getBounds().y + g.getFontMetrics().getAscent() / 2 + h / 2);
    }
}
