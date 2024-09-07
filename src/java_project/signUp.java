package java_project;

import java.awt.*;
import javax.swing.*;
import org.jdesktop.animation.timing.Animator;
import org.jdesktop.animation.timing.TimingTarget;
import org.jdesktop.animation.timing.TimingTargetAdapter;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import java_project.models.User;
import javax.swing.SwingWorker;

public class signUp extends javax.swing.JFrame {
    private Animator animatorSignUp;
    private boolean signUp;
    private JPanel homePagePanel;

    signUp() {
        initComponents();
        getContentPane().setBackground(new Color(245, 245, 245));
        initAnimation();
    }

    private void initComponents() {
        this.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
        this.setSize(800, 600);
        this.setTitle("ClassRoom");
        this.setLocationRelativeTo(null);
        this.setLayout(null);

        background1 = new components.Background();
        panelLogin = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        cmdSignIn = new components.Button("Sign Up");
        btnSignIn = new components.Button("Already have an account? Login");
        txtEmail = new components.TextField();
        txtName = new components.TextField();
        txtPass = new components.PasswordField();

        background1.setLayout(new java.awt.CardLayout());

        panelLogin.setOpaque(false);
        jPanel1.setOpaque(false);

        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("Sign Up");

        cmdSignIn.setBackground(new java.awt.Color(157, 153, 255));
        cmdSignIn.setForeground(new java.awt.Color(255, 255, 255));
        cmdSignIn.setEffectColor(new java.awt.Color(199, 196, 255));
        cmdSignIn.addActionListener(evt -> cmdSignInActionPerformed(evt));

        btnSignIn.setPreferredSize(new Dimension(300, 40));
        btnSignIn.setBackground(new Color(245, 245, 245));
        btnSignIn.setForeground(new Color(31, 130, 228));
        btnSignIn.setBorder(null);
        btnSignIn.setEffectColor(null);
        btnSignIn.setFont(btnSignIn.getFont().deriveFont(14f).deriveFont(java.awt.Font.ITALIC));
        btnSignIn.addActionListener(evt -> btnSignInActionPerformed(evt));

        txtName.setBackground(new java.awt.Color(245, 245, 245));
        txtName.setLabelText("Name");
        txtName.setLineColor(new java.awt.Color(131, 126, 253));
        txtName.setSelectionColor(new java.awt.Color(157, 153, 255));

        txtEmail.setBackground(new java.awt.Color(245, 245, 245));
        txtEmail.setLabelText("Email");
        txtEmail.setLineColor(new java.awt.Color(131, 126, 253));
        txtEmail.setSelectionColor(new java.awt.Color(157, 153, 255));

        txtPass.setBackground(new java.awt.Color(245, 245, 245));
        txtPass.setLabelText("Password");
        txtPass.setLineColor(new java.awt.Color(131, 126, 253));
        txtPass.setSelectionColor(new java.awt.Color(157, 153, 255));

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
                jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addGap(20, 20, 20)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                        .addComponent(txtPass, javax.swing.GroupLayout.DEFAULT_SIZE,
                                                javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(txtEmail, javax.swing.GroupLayout.DEFAULT_SIZE,
                                                javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(txtName, javax.swing.GroupLayout.DEFAULT_SIZE,
                                                javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(cmdSignIn, javax.swing.GroupLayout.DEFAULT_SIZE,
                                                javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(btnSignIn, javax.swing.GroupLayout.DEFAULT_SIZE,
                                                javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, 310,
                                                Short.MAX_VALUE))
                                .addGap(20, 20, 20)));
        jPanel1Layout.setVerticalGroup(
                jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(20, 20, 20)
                                .addComponent(jLabel1)
                                .addGap(20, 20, 20)
                                .addComponent(txtName, javax.swing.GroupLayout.PREFERRED_SIZE,
                                        javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(txtEmail, javax.swing.GroupLayout.PREFERRED_SIZE,
                                        javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtPass, javax.swing.GroupLayout.PREFERRED_SIZE,
                                        javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 20,
                                        Short.MAX_VALUE)
                                .addComponent(cmdSignIn, javax.swing.GroupLayout.PREFERRED_SIZE, 40,
                                        javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnSignIn, javax.swing.GroupLayout.PREFERRED_SIZE, 40,
                                        javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addContainerGap()));

        javax.swing.GroupLayout panelLoginLayout = new javax.swing.GroupLayout(panelLogin);
        panelLogin.setLayout(panelLoginLayout);
        panelLoginLayout.setHorizontalGroup(
                panelLoginLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(panelLoginLayout.createSequentialGroup()
                                .addContainerGap(427, Short.MAX_VALUE)
                                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE,
                                        javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addContainerGap(428, Short.MAX_VALUE)));
        panelLoginLayout.setVerticalGroup(
                panelLoginLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(panelLoginLayout.createSequentialGroup()
                                .addContainerGap(63, Short.MAX_VALUE)
                                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE,
                                        javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addContainerGap(233, Short.MAX_VALUE)));

        background1.add(panelLogin, "card2");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(background1, javax.swing.GroupLayout.DEFAULT_SIZE,
                                javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE));
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(background1, javax.swing.GroupLayout.DEFAULT_SIZE,
                                javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE));

        pack();
        setLocationRelativeTo(null);
    }

    private void cmdSignInActionPerformed(java.awt.event.ActionEvent evt) {
        if (!animatorSignUp.isRunning()) {
            signUp = true;
            String user = txtEmail.getText().trim();
            String name = txtName.getText().trim();
            String pass = String.valueOf(txtPass.getPassword());
            boolean action = true;

            if (name.equals("")) {
                txtEmail.setHelperText("Please input username");
                txtEmail.grabFocus();
                action = false;
            }
            if (user.equals("")) {
                txtEmail.setHelperText("Please input Email");
                txtEmail.grabFocus();
                action = false;
            }
            if (pass.equals("")) {
                txtPass.setHelperText("Please input password");
                if (action) {
                    txtPass.grabFocus();
                }
                action = false;
            }
            if (action) {
                cmdSignIn.setText("Loading...");
                new SwingWorker<Void, Void>() {
                    @Override
                    protected Void doInBackground() {
                        signUpWithServer(name, user, pass);
                        return null;
                    }

                    @Override
                    protected void done() {
                        cmdSignIn.setText("Sign In");
                        cmdSignIn.setEnabled(true);
                    }
                }.execute();
            }
        }
    }

    @SuppressWarnings("unchecked")
    private void signUpWithServer(String name, String email, String pass) {
        try {
            JSONObject json = new JSONObject();
            json.put("name", name);
            json.put("email", email);
            json.put("password", pass);

            String urlParameters = json.toString();
            String response = Utility.executePost("http://localhost:5000/api/users/register", urlParameters);
            System.out.println("response: " + response);
            if (response != null && !response.isEmpty()) {
                JSONParser parser = new JSONParser();
                JSONObject responseObject = (JSONObject) parser.parse(response);
                JSONObject userJson = (JSONObject) responseObject.get("user");

                if (userJson != null) {
                    String id = String.valueOf(userJson.get("id"));
                    User userObj = new User(id, name, email);

                    SwingUtilities.invokeLater(() -> {
                        animatorSignUp.start();
                        this.userObj = userObj;
                    });
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            SwingUtilities.invokeLater(() -> {
                cmdSignIn.setText("Sign In");
                cmdSignIn.setEnabled(true);
            });
        }
    }

    private User userObj;

    private void showHomePage(User user) {
        if (homePagePanel == null) {
            homePagePanel = new HomePage(user);
            background1.add(homePagePanel, "card1");
        }

        CardLayout cl = (CardLayout) (background1.getLayout());
        cl.show(background1, "card1");
    }

    private void initAnimation() {
        TimingTarget targetLogin = new TimingTargetAdapter() {
            @Override
            public void timingEvent(float fraction) {
                background1.setAnimate(signUp ? fraction : 1f - fraction);
                jLabel1.setVisible(false);
                txtEmail.setVisible(false);
                txtName.setVisible(false);
                txtPass.setVisible(false);
                cmdSignIn.setVisible(false);
                btnSignIn.setVisible(false);
            }

            @Override
            public void end() {
                showHomePage(userObj);
            }
        };

        animatorSignUp = new Animator(500, targetLogin);
        animatorSignUp.setResolution(0);
    }

    private void btnSignInActionPerformed(java.awt.event.ActionEvent evt) {

        this.dispose();
        new log_user().setVisible(true);
    }

    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(() -> {
            new signUp().setVisible(true);
        });
    }

    private components.Background background1;
    private javax.swing.JPanel panelLogin;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JLabel jLabel1;
    private components.Button cmdSignIn;
    private components.Button btnSignIn;
    private components.TextField txtEmail;
    private components.TextField txtName;
    private components.PasswordField txtPass;
}
