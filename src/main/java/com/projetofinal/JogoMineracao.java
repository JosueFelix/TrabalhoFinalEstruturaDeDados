package com.projetofinal;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class JogoMineracao extends JPanel implements KeyListener, MouseMotionListener, MouseListener {

    private final int TILE_SIZE = 48;
    private final int COLS = 20;
    private final int ROWS = 12;

    private static final int AIR = 0;
    private static final int STONE = 1;
    private static final int WOOD = 2;
    private static final int DIRT = 3;
    private static final int BRICK = 4;

    private int[][] map;

    private final List<int[][]> mundos = new ArrayList<>();
    private int mundoAtual = 0;

    private int playerX = 2;
    private int playerY = 4;
    private static final int PLAYER_HEIGHT = 2;

    private boolean leftPressed = false;
    private boolean rightPressed = false;
    private int jumpRemaining = 0;
    private boolean facingRight = true;

    private int mouseX = 0;
    private int mouseY = 0;

    private final Timer timer;

    private static final int TOOL_PICKAXE = 1;
    private static final int TOOL_AXE = 2;
    private static final int TOOL_SHOVEL = 3;
    private int ferramentaAtual = TOOL_PICKAXE;

    private final MeuHashTable<String, Integer> inventario;

    public JogoMineracao() {
        this.setPreferredSize(new Dimension(COLS * TILE_SIZE, ROWS * TILE_SIZE + 80));
        this.setBackground(Color.CYAN);
        this.addKeyListener(this);
        this.addMouseMotionListener(this);
        this.addMouseListener(this);
        this.setFocusable(true);

        inventario = new MeuHashTable<>(20);

        inventario.put("Picareta", 1);
        inventario.put("Machado", 1);
        inventario.put("Pa", 1);

        int[][] primeiro = gerarMapaTerrariaLike();
        mundos.add(primeiro);
        map = primeiro;

        timer = new Timer(80, e -> atualizarFisica());
        timer.start();
    }

    private boolean blocoSolido(int x, int y) {
        if (x < 0 || x >= COLS || y < 0 || y >= ROWS) {
            return true;
        }
        int tile = map[x][y];
        return tile == STONE || tile == WOOD || tile == DIRT || tile == BRICK;
    }

    private boolean espacoLivre(int x, int bottomY) {
        if (x < 0 || x >= COLS) {
            return false;
        }
        for (int h = 0; h < PLAYER_HEIGHT; h++) {
            int y = bottomY - h;
            if (y < 0 || blocoSolido(x, y)) {
                return false;
            }
        }
        return true;
    }

    private boolean estaNoChao() {
        int abaixo = playerY + 1;
        if (abaixo >= ROWS) {
            return true;
        }
        return blocoSolido(playerX, abaixo);
    }

    private void atualizarFisica() {
        if (leftPressed) {
            if (playerX > 0) {
                int novoX = playerX - 1;
                if (espacoLivre(novoX, playerY)) {
                    playerX = novoX;
                }
            } else {
                mudarDeMundo(-1);
            }
        }
        if (rightPressed) {
            if (playerX < COLS - 1) {
                int novoX = playerX + 1;
                if (espacoLivre(novoX, playerY)) {
                    playerX = novoX;
                }
            } else {
                mudarDeMundo(1);
            }
        }

        if (jumpRemaining > 0) {
            int novoY = playerY - 1;
            if (espacoLivre(playerX, novoY)) {
                playerY = novoY;
                jumpRemaining--;
            } else {
                jumpRemaining = 0;
            }
        } else {
            int abaixo = playerY + 1;
            if (abaixo < ROWS && espacoLivre(playerX, abaixo)) {
                playerY = abaixo;
            }
        }

        repaint();
    }

    private void mudarDeMundo(int delta) {
        int novoIndice = mundoAtual + delta;
        if (novoIndice < 0) {
            int[][] novo = gerarMapaTerrariaLike();
            mundos.add(0, novo);
            mundoAtual = 0;
        } else if (novoIndice >= mundos.size()) {
            int[][] novo = gerarMapaTerrariaLike();
            mundos.add(novo);
            mundoAtual = mundos.size() - 1;
        } else {
            mundoAtual = novoIndice;
        }

        map = mundos.get(mundoAtual);

        if (delta > 0) {
            playerX = 1;
        } else {
            playerX = COLS - 2;
        }

        while (!espacoLivre(playerX, playerY) && playerY > 0) {
            playerY--;
        }
    }

    private int[][] gerarMapaTerrariaLike() {
        int[][] novo = new int[COLS][ROWS];
        Random rnd = new Random();

        for (int x = 0; x < COLS; x++) {
            for (int y = 0; y < ROWS; y++) {
                novo[x][y] = AIR;
            }
        }

        int baseGround = ROWS - 4;
        int h = baseGround;
        int[] surface = new int[COLS];

        for (int x = 0; x < COLS; x++) {
            h += rnd.nextInt(5) - 2;
            if (h < 1) h = 1;
            if (h > ROWS - 3) h = ROWS - 3;
            surface[x] = h;

            int dirtThickness = 1 + rnd.nextInt(2);

            for (int d = 0; d < dirtThickness; d++) {
                int y = surface[x] + d;
                if (y >= 0 && y < ROWS) {
                    novo[x][y] = DIRT;
                }
            }

            int startStone = surface[x] + dirtThickness;
            for (int y = startStone; y < ROWS; y++) {
                novo[x][y] = STONE;
            }
        }

        for (int x = 2; x < COLS; x += 4) {
            if (rnd.nextDouble() < 0.75) {
                int altura = 2 + rnd.nextInt(2);
                int trunkBaseY = surface[x] - 1;
                for (int hTree = 1; hTree <= altura; hTree++) {
                    int y = trunkBaseY - (hTree - 1);
                    if (y >= 0 && y < ROWS) {
                        novo[x][y] = WOOD;
                    }
                }
            }
        }

        return novo;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        for (int x = 0; x < COLS; x++) {
            for (int y = 0; y < ROWS; y++) {
                int tile = map[x][y];
                int px = x * TILE_SIZE;
                int py = y * TILE_SIZE;
                switch (tile) {
                    case STONE -> desenharBlocoPedra(g, px, py);
                    case WOOD -> desenharBlocoMadeira(g, px, py);
                    case DIRT -> desenharBlocoTerra(g, px, py);
                    case BRICK -> desenharBlocoTijolo(g, px, py);
                    default -> {
                    }
                }
            }
        }

        int targetX = mouseX / TILE_SIZE;
        int targetY = mouseY / TILE_SIZE;
        if (targetX >= 0 && targetX < COLS && targetY >= 0 && targetY < ROWS) {
            g.setColor(Color.YELLOW);
            g.drawRect(targetX * TILE_SIZE, targetY * TILE_SIZE, TILE_SIZE, TILE_SIZE);
        }

        desenharPersonagem(g);

        desenharLegendaBlocos(g);

        g.setColor(Color.BLACK);
        g.drawString("WASD: mover/pular | Espaco: minerar bloco sob o mouse | 1/2/3 Ferramentas", 10, 20);
        g.drawString("S: salvar no BD | C: crafting | R/T: remover Pedra/Tijolo da hash", 10, 40);
        g.drawString("Ferramenta: " + nomeFerramentaAtual(), 10, 60);
    }

    private void desenharBlocoTijolo(Graphics g, int px, int py) {
        Color brick = new Color(178, 34, 34);
        Color mortar = new Color(200, 130, 115);

        g.setColor(brick);
        g.fillRect(px, py, TILE_SIZE, TILE_SIZE);

        g.setColor(mortar);
        int rows = 3;
        int cols = 2;
        int h = TILE_SIZE / (rows + 1);
        int w = TILE_SIZE / (cols + 1);
        for (int r = 0; r < rows; r++) {
            int y = py + r * (h + 2) + 4;
            for (int c = 0; c < cols; c++) {
                int x = px + c * (w + 4) + 4;
                g.fillRect(x, y, w, h);
            }
        }

        g.setColor(Color.BLACK);
        g.drawRect(px, py, TILE_SIZE, TILE_SIZE);
    }

    private void desenharPersonagem(Graphics g) {
        int spriteWidth = TILE_SIZE;
        int spriteHeight = TILE_SIZE * PLAYER_HEIGHT;
        int left = playerX * TILE_SIZE;
        int bottom = (playerY + 1) * TILE_SIZE;
        int top = bottom - spriteHeight;

        g.setColor(new Color(120, 70, 20));
        g.fillRect(left + 4, top, spriteWidth - 8, 10);

        g.setColor(new Color(247, 208, 75));
        g.fillRect(left + 4, top + 10, spriteWidth - 8, 20);

        g.setColor(Color.BLACK);
        g.fillRect(left + 10, top + 18, 4, 4);
        g.fillRect(left + spriteWidth - 14, top + 18, 4, 4);

        g.setColor(new Color(0, 158, 96));
        g.fillRect(left + 3, top + 30, spriteWidth - 6, 20);

        g.setColor(new Color(247, 208, 75));
        g.fillRect(left + 2, top + 32, 6, 16);
        g.fillRect(left + spriteWidth - 8, top + 32, 6, 16);

        g.setColor(new Color(0, 76, 153));
        g.fillRect(left + 6, top + 50, spriteWidth - 12, spriteHeight - 50);

        g.fillRect(left + 6, bottom - 18, 8, 18);
        g.fillRect(left + spriteWidth - 14, bottom - 18, 8, 18);

        int handX = left + spriteWidth;
        int handY = top + 40;
        desenharFerramentaNaMao(g, handX, handY);
    }

    private void desenharBlocoMadeira(Graphics g, int px, int py) {
        Color base = new Color(139, 69, 19);
        Color claro = new Color(181, 101, 29);
        Color escuro = new Color(92, 51, 23);

        g.setColor(base);
        g.fillRect(px, py, TILE_SIZE, TILE_SIZE);

        g.setColor(claro);
        int passo = TILE_SIZE / 6;
        for (int x = px + passo; x < px + TILE_SIZE; x += passo * 2) {
            g.fillRect(x, py, passo / 2, TILE_SIZE);
        }

        g.setColor(escuro);
        for (int x = px + passo * 2; x < px + TILE_SIZE; x += passo * 2) {
            g.drawLine(x, py, x, py + TILE_SIZE);
        }

        int cx = px + TILE_SIZE / 2 - passo / 2;
        int cy = py + TILE_SIZE / 2 - passo / 2;
        g.setColor(claro);
        g.fillOval(cx, cy, passo, passo);
        g.setColor(escuro);
        g.drawOval(cx, cy, passo, passo);

        g.setColor(Color.BLACK);
        g.drawRect(px, py, TILE_SIZE, TILE_SIZE);
    }

    private void desenharBlocoPedra(Graphics g, int px, int py) {
        Color lightGray = new Color(200, 200, 200);
        Color darkGray = new Color(90, 90, 90);

        g.setColor(lightGray);
        g.fillRect(px, py, TILE_SIZE, TILE_SIZE);

        g.setColor(darkGray);
        int s = TILE_SIZE / 5;

        g.fillRect(px + s,         py + s,         s * 2, s);
        g.fillRect(px + s * 3,     py + s / 2,     s * 2, s * 2);
        g.fillRect(px + s / 2,     py + s * 2,     s * 2, s * 2);
        g.fillRect(px + s * 3,     py + s * 3,     s * 2, s * 2);
        g.fillRect(px + s * 2,     py + s * 4,     s * 2, s);

        g.setColor(Color.DARK_GRAY);
        g.drawRect(px, py, TILE_SIZE, TILE_SIZE);
    }

    private void desenharBlocoTerra(Graphics g, int px, int py) {
        Color grass = new Color(34, 177, 76);
        Color dirt = new Color(150, 75, 40);

        int grassHeight = TILE_SIZE / 3;

        g.setColor(dirt);
        g.fillRect(px, py + grassHeight, TILE_SIZE, TILE_SIZE - grassHeight);

        g.setColor(grass);
        g.fillRect(px, py, TILE_SIZE, grassHeight);

        int step = TILE_SIZE / 6;
        int base = py + grassHeight;

        g.fillRect(px + step,       base - step,     step * 2, step);
        g.fillRect(px + step * 3,   base - step * 2, step * 2, step * 2);
        g.fillRect(px + step * 5,   base - step * 2, step,     step * 2);

        g.setColor(Color.BLACK);
        g.drawRect(px, py, TILE_SIZE, TILE_SIZE);
    }

    private void desenharFerramentaNaMao(Graphics g, int baseX, int baseY) {
        g.setColor(new Color(120, 72, 24));
        g.fillRect(baseX, baseY - 8, 4, 20);

        g.setColor(Color.LIGHT_GRAY);

        if (ferramentaAtual == TOOL_AXE) {
            g.fillRect(baseX + 3, baseY - 12, 10, 6);
            g.fillRect(baseX + 5, baseY - 6, 8, 4);
        } else if (ferramentaAtual == TOOL_SHOVEL) {
            g.fillRect(baseX - 2, baseY - 18, 8, 10);
        } else if (ferramentaAtual == TOOL_PICKAXE) {
            g.fillRect(baseX - 6, baseY - 14, 18, 4);
            g.fillRect(baseX + 2, baseY - 14, 4, 10);
        }
    }

    private void desenharLegendaBlocos(Graphics g) {
        int blocoSize = 30;
        int inicioX = 10;
        int yBase = getHeight() - blocoSize - 20;

        g.setColor(new Color(139, 69, 19));
        g.fillRect(inicioX, yBase, blocoSize, blocoSize);
        g.setColor(Color.BLACK);
        g.drawRect(inicioX, yBase, blocoSize, blocoSize);

        int xTerra = inicioX + blocoSize + 20;
        g.setColor(new Color(139, 115, 85));
        g.fillRect(xTerra, yBase, blocoSize, blocoSize);
        g.setColor(Color.BLACK);
        g.drawRect(xTerra, yBase, blocoSize, blocoSize);

        int xPedra = xTerra + blocoSize + 20;
        g.setColor(Color.GRAY);
        g.fillRect(xPedra, yBase, blocoSize, blocoSize);
        g.setColor(Color.BLACK);
        g.drawRect(xPedra, yBase, blocoSize, blocoSize);

        int xTijolo = xPedra + blocoSize + 20;
        desenharBlocoTijolo(g, xTijolo, yBase);

        g.drawString("Madeira / Terra / Pedra / Tijolo", inicioX, yBase + blocoSize + 18);
    }

    private String nomeFerramentaAtual() {
        return switch (ferramentaAtual) {
            case TOOL_PICKAXE -> "Picareta";
            case TOOL_AXE -> "Machado";
            case TOOL_SHOVEL -> "Pa";
            default -> "Desconhecida";
        };
    }

    private void minar() {
        int targetX = mouseX / TILE_SIZE;
        int targetY = mouseY / TILE_SIZE;

        if (targetX < 0 || targetX >= COLS || targetY < 0 || targetY >= ROWS) {
            System.out.println("Nada para minerar nessa direção.");
            return;
        }

        int tipoBloco = map[targetX][targetY];

        if (tipoBloco == AIR) {
            System.out.println("Nenhum bloco nessa direção.");
            return;
        }

        String recurso = null;
        boolean podeQuebrar = false;

        if (tipoBloco == STONE) {
            if (ferramentaAtual == TOOL_PICKAXE) {
                recurso = "Pedra";
                podeQuebrar = true;
            } else {
                System.out.println("Use a PICARETA (1) para quebrar pedra.");
            }
        } else if (tipoBloco == WOOD) {
            if (ferramentaAtual == TOOL_AXE) {
                recurso = "Madeira";
                podeQuebrar = true;
            } else {
                System.out.println("Use o MACHADO (2) para quebrar madeira.");
            }
        } else if (tipoBloco == DIRT) {
            if (ferramentaAtual == TOOL_SHOVEL) {
                recurso = "Terra";
                podeQuebrar = true;
            } else {
                System.out.println("Use a PA (3) para cavar terra.");
            }
        }

        if (!podeQuebrar || recurso == null) {
            return;
        }

        Integer qtdAtual = inventario.get(recurso);
        if (qtdAtual == null) {
            qtdAtual = 0;
        }

        inventario.put(recurso, qtdAtual + 1);

        map[targetX][targetY] = AIR;

        System.out.println("Coletado: " + recurso + ". Total: " + (qtdAtual + 1));
        inventario.printInventory();
    }

    private void salvarNoSupabase() {
        System.out.println("Enviando dados para o banco local...");
        Integer pedras = inventario.get("Pedra");
        Integer madeiras = inventario.get("Madeira");
        Integer terras = inventario.get("Terra");

        if (pedras != null) {
            SupabaseManager.salvarInventario("Pedra", pedras);
        }
        if (madeiras != null) {
            SupabaseManager.salvarInventario("Madeira", madeiras);
        }
        if (terras != null) {
            SupabaseManager.salvarInventario("Terra", terras);
        }
    }

    private void crafting() {
        final int REQ_PEDRA = 3;
        final int REQ_MADEIRA = 1;
        final int REQ_TERRA = 2;

        int pedras = inventario.get("Pedra") == null ? 0 : inventario.get("Pedra");
        int madeiras = inventario.get("Madeira") == null ? 0 : inventario.get("Madeira");
        int terras = inventario.get("Terra") == null ? 0 : inventario.get("Terra");

        List<String> faltando = new ArrayList<>();
        if (pedras < REQ_PEDRA) faltando.add("Pedra(" + (REQ_PEDRA - pedras) + ")");
        if (madeiras < REQ_MADEIRA) faltando.add("Madeira(" + (REQ_MADEIRA - madeiras) + ")");
        if (terras < REQ_TERRA) faltando.add("Terra(" + (REQ_TERRA - terras) + ")");

        if (!faltando.isEmpty()) {
            System.out.println("Recursos insuficientes para criar Tijolo. Faltando: " + String.join(", ", faltando));
            return;
        }

        inventario.put("Pedra", pedras - REQ_PEDRA);
        inventario.put("Madeira", madeiras - REQ_MADEIRA);
        inventario.put("Terra", terras - REQ_TERRA);

        Integer tijolos = inventario.get("Tijolo");
        if (tijolos == null) tijolos = 0;
        inventario.put("Tijolo", tijolos + 1);

        System.out.println("Crafting realizado: Tijolo criado! Total tijolos: " + (tijolos + 1));
        inventario.printInventory();
    }

    private void removerRecurso(String recurso) {
        Integer qtd = inventario.get(recurso);
        if (qtd == null) {
            System.out.println("Não há " + recurso + " no inventário para remover.");
            return;
        }
        inventario.remove(recurso);
        System.out.println("Recurso removido da tabela hash: " + recurso);
        inventario.printInventory();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();

        if (key == KeyEvent.VK_A) {
            leftPressed = true;
            facingRight = false;
        }
        if (key == KeyEvent.VK_D) {
            rightPressed = true;
            facingRight = true;
        }
        if (key == KeyEvent.VK_W && estaNoChao()) {
            jumpRemaining = 3;
        }

        if (key == KeyEvent.VK_1) {
            ferramentaAtual = TOOL_PICKAXE;
            System.out.println("Ferramenta atual: Picareta");
        }
        if (key == KeyEvent.VK_2) {
            ferramentaAtual = TOOL_AXE;
            System.out.println("Ferramenta atual: Machado");
        }
        if (key == KeyEvent.VK_3) {
            ferramentaAtual = TOOL_SHOVEL;
            System.out.println("Ferramenta atual: Pa");
        }

        if (key == KeyEvent.VK_SPACE) {
            minar();
        }

        if (key == KeyEvent.VK_S) {
            salvarNoSupabase();
        }

        if (key == KeyEvent.VK_C) {
            crafting();
        }

        if (key == KeyEvent.VK_R) {
            removerRecurso("Pedra");
        }
        if (key == KeyEvent.VK_T) {
            removerRecurso("Tijolo");
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int key = e.getKeyCode();
        if (key == KeyEvent.VK_A) {
            leftPressed = false;
        }
        if (key == KeyEvent.VK_D) {
            rightPressed = false;
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        mouseX = e.getX();
        mouseY = e.getY();
        repaint();
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        mouseX = e.getX();
        mouseY = e.getY();
        repaint();
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON3) {
            int tx = e.getX() / TILE_SIZE;
            int ty = e.getY() / TILE_SIZE;
            if (tx < 0 || tx >= COLS || ty < 0 || ty >= ROWS) {
                return;
            }
            if (map[tx][ty] != AIR) {
                System.out.println("Não é possível colocar aí. Espaço ocupado.");
                return;
            }

            Integer qtd = inventario.get("Tijolo");
            if (qtd == null || qtd <= 0) {
                System.out.println("Você não tem Tijolos para colocar.");
                return;
            }

            inventario.put("Tijolo", qtd - 1);
            map[tx][ty] = BRICK;
            System.out.println("Tijolo colocado em (" + tx + "," + ty + "). Restam: " + (qtd - 1));
            inventario.printInventory();
            repaint();
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Mineração Hash - Projeto Final");
        JogoMineracao game = new JogoMineracao();
        frame.add(game);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}


