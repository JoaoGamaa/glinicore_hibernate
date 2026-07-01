package controller;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.imageio.ImageIO;
import javax.imageio.stream.ImageOutputStream;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;

public class FuncoesUteis {

    private static final DateFormat FORMATO_DATA = new SimpleDateFormat("dd/MM/yyyy");
    private static final DateFormat FORMATO_DATA_HORA = new SimpleDateFormat("dd/MM/yyyy HH:mm");

    static {
        FORMATO_DATA.setLenient(false);
        FORMATO_DATA_HORA.setLenient(false);
    }

    public static String limparMascara(String texto) {
        return texto == null ? "" : texto.replaceAll("[^0-9A-Za-z@._\\- ]", "").trim();
    }

    public static String somenteNumeros(String texto) {
        return texto == null ? "" : texto.replaceAll("\\D", "");
    }

    public static Date strToDate(String strData) throws ParseException {
        if (strData == null || strData.isBlank() || strData.contains("_")) {
            return null;
        }
        return FORMATO_DATA.parse(strData);
    }

    public static String dateToStr(Date data) {
        return data == null ? "" : FORMATO_DATA.format(data);
    }

    public static String dateTimeToStr(Date data) {
        return data == null ? "" : FORMATO_DATA_HORA.format(data);
    }

    public static String moeda(BigDecimal valor) {
        if (valor == null) return NumberFormat.getCurrencyInstance().format(BigDecimal.ZERO);
        return NumberFormat.getCurrencyInstance().format(valor);
    }

    public static BigDecimal strToBigDecimal(String valor) {
        if (valor == null || valor.isBlank()) return BigDecimal.ZERO;
        String normalizado = valor.replace("R$", "").replace(".", "").replace(",", ".").trim();
        return new BigDecimal(normalizado);
    }

    public static boolean cpfBasicoValido(String cpf) {
        String numeros = somenteNumeros(cpf);
        return numeros.length() == 11;
    }

    public static void mostrarFoto(JLabel lbl, Icon icon) {
        if (icon == null || lbl == null) return;
        ImageIcon imagem = (ImageIcon) icon;
        imagem.setImage(imagem.getImage().getScaledInstance(lbl.getWidth(), lbl.getHeight(), Image.SCALE_SMOOTH));
        lbl.setText("");
        lbl.setIcon(imagem);
    }

    public static byte[] iconToBytes(Icon icon) {
        if (icon == null) return null;
        BufferedImage img = new BufferedImage(icon.getIconWidth(), icon.getIconHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = img.createGraphics();
        icon.paintIcon(null, g2d, 0, 0);
        g2d.dispose();
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            ImageOutputStream ios = ImageIO.createImageOutputStream(baos);
            try {
                ImageIO.write(img, "png", ios);
            } finally {
                ios.close();
            }
            return baos.toByteArray();
        } catch (IOException ex) {
            return null;
        }
    }

    public static ImageIcon bytesToIcon(byte[] bytes) {
        if (bytes == null || bytes.length == 0) return null;
        return new ImageIcon(bytes);
    }
}
