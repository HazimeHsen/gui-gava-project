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


public class log_user extends javax.swing.JFrame {
    private Animator animatorLogin;
    private boolean signIn;
    private JPanel homePagePanel;

    log_user() {
        initComponents();
        getContentPane().setBackground(new Color(245, 245, 245));
        initAnimation();
    }

    private void initComponents() {
        this.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
        this.setSize(800, 600);
        this.setTitle("Login Page");
        this.setLocationRelativeTo(null);
        this.setLayout(null);

        background1 = new components.Background();
        panelLogin = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        cmdSignIn = new components.Button();
        txtUser = new components.TextField();
        txtPass = new components.PasswordField();

        background1.setLayout(new java.awt.CardLayout());

        panelLogin.setOpaque(false);
        jPanel1.setOpaque(false);

        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("Login");

        cmdSignIn.setBackground(new java.awt.Color(157, 153, 255));
        cmdSignIn.setForeground(new java.awt.Color(255, 255, 255));
        cmdSignIn.setText("Sign In");
        cmdSignIn.setEffectColor(new java.awt.Color(199, 196, 255));
        cmdSignIn.addActionListener(evt -> cmdSignInActionPerformed(evt));

        txtUser.setBackground(new java.awt.Color(245, 245, 245));
        txtUser.setLabelText("User Name");
        txtUser.setLineColor(new java.awt.Color(131, 126, 253));
        txtUser.setSelectionColor(new java.awt.Color(157, 153, 255));
        txtUser.setText("hsen@gmail.com");
        txtPass.setBackground(new java.awt.Color(245, 245, 245));
        txtPass.setLabelText("Password");
        txtPass.setLineColor(new java.awt.Color(131, 126, 253));
        txtPass.setSelectionColor(new java.awt.Color(157, 153, 255));
        txtPass.setText("hazime18");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
                jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addGap(20, 20, 20)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                        .addComponent(txtPass, javax.swing.GroupLayout.DEFAULT_SIZE,
                                                javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(txtUser, javax.swing.GroupLayout.DEFAULT_SIZE,
                                                javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(cmdSignIn, javax.swing.GroupLayout.DEFAULT_SIZE,
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
                                .addComponent(txtUser, javax.swing.GroupLayout.PREFERRED_SIZE,
                                        javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtPass, javax.swing.GroupLayout.PREFERRED_SIZE,
                                        javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 30,
                                        Short.MAX_VALUE)
                                .addComponent(cmdSignIn, javax.swing.GroupLayout.PREFERRED_SIZE, 40,
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
        if (!animatorLogin.isRunning()) {
            signIn = true;
            String user = txtUser.getText().trim();
            String pass = String.valueOf(txtPass.getPassword());
            boolean action = true;

            if (user.equals("")) {
                txtUser.setHelperText("Please input user name");
                txtUser.grabFocus();
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
//                enableLogin(false);
//                cmdSignIn.setText("Loading...");

                new SwingWorker<Void, Void>() {
                    @Override
                    protected Void doInBackground() {
                        signInWithServer(user, pass);
                        return null;
                    }

                    @Override
                    protected void done() {
                        cmdSignIn.setText("Sign In");
                        enableLogin(true);
                       
                    }
                }.execute();
            }
        }
    }

    private void enableLogin(boolean action) {
        txtUser.setEditable(action);
        txtPass.setEditable(action);
        cmdSignIn.setEnabled(action);
    }

    @SuppressWarnings("unchecked")
    private void signInWithServer(String user, String pass) {
        try {
            JSONObject json = new JSONObject();
            json.put("email", user);
            json.put("password", pass);

            String urlParameters = json.toString();
            String response = Utility.executePost("http://localhost:5000/api/users/login", urlParameters);
            System.out.println("response: " + response);
            if (response != null && !response.isEmpty()) {
                JSONParser parser = new JSONParser();
                JSONObject responseObject = (JSONObject) parser.parse(response);
                JSONObject userJson = (JSONObject) responseObject.get("user");

                if (userJson != null) {
                    String id = String.valueOf(userJson.get("id"));
                    String name = (String) userJson.get("name");
                    String email = (String) userJson.get("email");
                    User userObj = new User(id, name, email);

                    SwingUtilities.invokeLater(() -> {
                        animatorLogin.start();
                        this.userObj = userObj;
                    });
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
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
                background1.setAnimate(signIn ? fraction : 1f - fraction);
                jLabel1.setVisible(false);
                txtUser.setVisible(false);
                txtPass.setVisible(false);
                cmdSignIn.setVisible(false);
               
            }

            @Override
            public void end() {
                showHomePage(userObj);
            }
        };

        animatorLogin = new Animator(500, targetLogin);
        animatorLogin.setResolution(0);
    }

    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(() -> {
            new log_user().setVisible(true);
        });
    }

    private components.Background background1;
    private javax.swing.JPanel panelLogin;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JLabel jLabel1;
    private components.Button cmdSignIn;
    private components.TextField txtUser;
    private components.PasswordField txtPass;
}
