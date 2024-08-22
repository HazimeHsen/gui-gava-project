/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package java_project.models;

import javax.swing.*;
import java.awt.*;

public class CardExample extends JFrame {

    // Constructor to set up the UI
    public CardExample() {
        setTitle("Card Layout Example");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Create a CardLayout container
        JPanel cardPanel = new JPanel();
        CardLayout cardLayout = new CardLayout();
        cardPanel.setLayout(cardLayout);

        // Create cards
        JPanel card1 = createCard("Card 1", "This is the first card.");
        JPanel card2 = createCard("Card 2", "This is the second card.");
        JPanel card3 = createCard("Card 3", "This is the third card.");

        // Add cards to the card panel
        cardPanel.add(card1, "Card1");
        cardPanel.add(card2, "Card2");
        cardPanel.add(card3, "Card3");

        // Create navigation buttons
        JButton showCard1 = new JButton("Show Card 1");
        JButton showCard2 = new JButton("Show Card 2");
        JButton showCard3 = new JButton("Show Card 3");

        showCard1.addActionListener(e -> cardLayout.show(cardPanel, "Card1"));
        showCard2.addActionListener(e -> cardLayout.show(cardPanel, "Card2"));
        showCard3.addActionListener(e -> cardLayout.show(cardPanel, "Card3"));

        // Create a panel for buttons
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(showCard1);
        buttonPanel.add(showCard2);
        buttonPanel.add(showCard3);

        // Add components to the frame
        add(cardPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    // Method to create a card
    private JPanel createCard(String title, String description) {
        JPanel card = new JPanel();
        card.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
        card.setLayout(new BorderLayout());

        JLabel titleLabel = new JLabel(title, JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        card.add(titleLabel, BorderLayout.NORTH);

        JTextArea descriptionArea = new JTextArea(description);
        descriptionArea.setEditable(false);
        descriptionArea.setWrapStyleWord(true);
        descriptionArea.setLineWrap(true);
        card.add(descriptionArea, BorderLayout.CENTER);

        return card;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new CardExample().setVisible(true);
        });
    }
}

