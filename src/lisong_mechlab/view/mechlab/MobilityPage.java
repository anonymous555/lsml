/*
 * @formatter:off
 * Li Song Mechlab - A 'mech building tool for PGI's MechWarrior: Online.
 * Copyright (C) 2013  Emily Björk
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
//@formatter:on
package lisong_mechlab.view.mechlab;

import java.awt.BorderLayout;
import java.util.Collection;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

import lisong_mechlab.model.chassi.MovementProfile;
import lisong_mechlab.model.item.Engine;
import lisong_mechlab.model.loadout.LoadoutBase;
import lisong_mechlab.model.metrics.TopSpeed;
import lisong_mechlab.model.metrics.TurningSpeed;
import lisong_mechlab.model.modifiers.Modifier;
import lisong_mechlab.util.message.Message;
import lisong_mechlab.util.message.MessageXBar;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

/**
 * This {@link JPanel} will render all mobility information about a loadout.
 * 
 * @author Emily Björk
 *
 */
public class MobilityPage extends JPanel implements Message.Recipient {
    private static final long    serialVersionUID  = -3878482179163239207L;

    private final AngleDisplay   torsoYawDisplay   = new AngleDisplay(90.0);
    private final AngleDisplay   torsoPitchDisplay = new AngleDisplay(0.0);

    private final JLabel         torsoYawAngle     = new JLabel("Torso yaw angle: ");
    private final JLabel         torsoPitchAngle   = new JLabel("Torso pitch angle: ");
    private final JLabel         torsoYawSpeed     = new JLabel("Torso yaw speed: ");
    private final JLabel         torsoPitchSpeed   = new JLabel("Torso pitch speed: ");

    private final JLabel         armYawAngle       = new JLabel("Arm yaw angle: ");
    private final JLabel         armPitchAngle     = new JLabel("Arm pitch angle: ");
    private final JLabel         armYawSpeed       = new JLabel("Arm yaw speed: ");
    private final JLabel         armPitchSpeed     = new JLabel("Arm pitch speed: ");

    private final LoadoutBase<?> loadout;
    private ChartPanel           chartPanel;

    public MobilityPage(LoadoutBase<?> aLoadout, MessageXBar aXBar) {
        aXBar.attach(this);
        loadout = aLoadout;

        add(makeTorsoPanel());
        add(makeMovementPanel());

        updatePanels();
    }

    private JPanel makeMovementPanel() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBorder(BorderFactory.createTitledBorder("Movement"));

        XYSeriesCollection dataset = new XYSeriesCollection();
        JFreeChart chart = ChartFactory.createXYLineChart("Turn speed", "Speed [km/h]", "Turn rate [°/s]", dataset,
                PlotOrientation.VERTICAL, true, false, false);
        chartPanel = new ChartPanel(chart);

        root.add(chartPanel, BorderLayout.WEST);
        return root;
    }

    private JPanel makeTorsoPanel() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBorder(BorderFactory.createTitledBorder("Arms & Torso"));

        JPanel diagrams = new JPanel();
        diagrams.add(torsoYawDisplay);
        diagrams.add(torsoPitchDisplay);

        JPanel numbers = new JPanel();
        numbers.setLayout(new BoxLayout(numbers, BoxLayout.PAGE_AXIS));
        numbers.add(torsoYawAngle);
        numbers.add(torsoPitchAngle);
        numbers.add(torsoYawSpeed);
        numbers.add(torsoPitchSpeed);
        numbers.add(armYawAngle);
        numbers.add(armPitchAngle);
        numbers.add(armYawSpeed);
        numbers.add(armPitchSpeed);

        root.add(numbers, BorderLayout.WEST);
        root.add(diagrams, BorderLayout.EAST);
        return root;
    }

    private void updatePanels() {
        Engine engine = loadout.getEngine();
        int rating = 0;
        if (engine != null)
            rating = engine.getRating();
        double mass = loadout.getChassis().getMassMax();

        Collection<Modifier> modifiers = loadout.getModifiers();
        double torso_pitch = loadout.getMovementProfile().getTorsoPitchMax(modifiers);
        double torso_yaw = loadout.getMovementProfile().getTorsoYawMax(modifiers);
        double torso_pitch_speed = loadout.getMovementProfile().getTorsoPitchSpeed(modifiers) * rating / mass;
        double torso_yaw_speed = loadout.getMovementProfile().getTorsoYawSpeed(modifiers) * rating / mass;

        double arm_pitch = loadout.getMovementProfile().getArmPitchMax(modifiers);
        double arm_yaw = loadout.getMovementProfile().getArmYawMax(modifiers);
        double arm_pitch_speed = loadout.getMovementProfile().getArmPitchSpeed(modifiers) * rating / mass;
        double arm_yaw_speed = loadout.getMovementProfile().getArmYawSpeed(modifiers) * rating / mass;

        torsoYawDisplay.updateAngles(torso_yaw, arm_yaw);
        torsoPitchDisplay.updateAngles(torso_pitch, arm_pitch);

        torsoYawAngle.setText("Torso yaw angle: " + LoadoutInfoPanel.df1.format(torso_yaw) + "°");
        torsoPitchAngle.setText("Torso pitch angle: " + LoadoutInfoPanel.df1.format(torso_pitch) + "°");
        torsoYawSpeed.setText("Torso yaw speed: " + LoadoutInfoPanel.df1.format(torso_yaw_speed) + "°/s");
        torsoPitchSpeed.setText("Torso pitch speed: " + LoadoutInfoPanel.df1.format(torso_pitch_speed) + "°/s");

        armYawAngle.setText("Arm yaw angle: " + LoadoutInfoPanel.df1.format(arm_yaw) + "°");
        armPitchAngle.setText("Arm pitch angle: " + LoadoutInfoPanel.df1.format(arm_pitch) + "°");
        armYawSpeed.setText("Arm yaw speed: " + LoadoutInfoPanel.df1.format(arm_yaw_speed) + "°/s");
        armPitchSpeed.setText("Arm pitch speed: " + LoadoutInfoPanel.df1.format(arm_pitch_speed) + "°/s");
        
        
        XYSeriesCollection turnSpeedGraph = new XYSeriesCollection();
        XYSeries turnSpeed = new XYSeries("Turn speed", true, false);

        MovementProfile mp = loadout.getMovementProfile();

        if (rating > 0) {
            double topSpeed = TopSpeed.calculate(rating, mp, mass, modifiers);
            double lowSpeed = mp.getTurnLerpLowSpeed(modifiers);
            double midSpeed = mp.getTurnLerpMidSpeed(modifiers);
            double highSpeed = mp.getTurnLerpHighSpeed(modifiers);
            turnSpeed.add(topSpeed * lowSpeed, TurningSpeed.getTurnRateAtThrottle(lowSpeed, rating, mass, mp, modifiers));
            turnSpeed.add(topSpeed * midSpeed, TurningSpeed.getTurnRateAtThrottle(midSpeed, rating, mass, mp, modifiers));
            turnSpeed.add(topSpeed * highSpeed, TurningSpeed.getTurnRateAtThrottle(highSpeed, rating, mass, mp, modifiers));
        }
        turnSpeedGraph.addSeries(turnSpeed);

        JFreeChart chart = ChartFactory.createXYLineChart("Turn speed", "Speed [km/h]", "Turn rate [°/s]", turnSpeedGraph,
                PlotOrientation.VERTICAL, true, false, false);
        chartPanel.setChart(chart);

    }

    @Override
    public void receive(Message aMsg) {
        if (aMsg.isForMe(loadout))
            updatePanels();
    }
}