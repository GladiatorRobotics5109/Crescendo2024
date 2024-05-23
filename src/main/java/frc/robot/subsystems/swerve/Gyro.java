package frc.robot.subsystems.swerve;

import edu.wpi.first.math.geometry.Rotation2d;
import org.littletonrobotics.junction.Logger;

public class Gyro {
    private final GyroIO m_io;
    private final GyroIOInputsAutoLogged m_inputs;

    public Gyro(GyroIO io) {
        m_io = io;
        m_inputs = new GyroIOInputsAutoLogged();
    }

    public Rotation2d getYaw() {
        return m_inputs.yaw;
    }

    public Rotation2d getRoll() {
        return m_inputs.roll;
    }

    public Rotation2d getPitch() {
        return m_inputs.pitch;
    }

    public boolean isConnected() {
        return m_inputs.isConnected;
    }

    public void periodic() {
        m_io.updateInputs(m_inputs);
        Logger.processInputs("Swerve/Gyro", m_inputs);
    }
}
