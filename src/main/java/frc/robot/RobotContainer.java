// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import frc.robot.util.Util;
import org.littletonrobotics.junction.networktables.LoggedDashboardChooser;

import com.pathplanner.lib.auto.NamedCommands;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.button.CommandPS5Controller;
import frc.robot.stateMachine.StateMachine;
import frc.robot.subsystems.swerve.SwerveSubsystem;
import frc.robot.subsystems.vision.VisionSubsystem;

public class RobotContainer {
    private VisionSubsystem m_vision;
    private SwerveSubsystem m_swerve;

    private CommandPS5Controller m_driverController;
    // private GenericHID m_driverController;

    private LoggedDashboardChooser<Command> m_autoChooser;

    public RobotContainer() {
        m_vision = new VisionSubsystem();
        m_swerve = new SwerveSubsystem();

        StateMachine.init(m_vision, m_swerve);

        m_driverController = new CommandPS5Controller(Constants.DriveTeamConstants.kDriverControllerPort);
        // m_driverController = new GenericHID(0);

        configureBindings();
        registerNamedCommands();

        m_autoChooser = new LoggedDashboardChooser<Command>("AutoChooser");

        m_autoChooser.addDefaultOption("DoNothing", AutoBuilder.doNothing(m_swerve));
        m_autoChooser.addOption("Test", AutoBuilder.test(m_swerve));
    }

    public Command commandGetAutonomous() {
        return m_autoChooser.get();
    }

    private void configureBindings() {
        m_swerve.setDefaultCommand(
            m_swerve.commandDriveWithJoystick(
                m_driverController::getLeftX,
                m_driverController::getLeftY,
                m_driverController::getRightX,
                () -> Constants.TeleopConstants.kDriveFieldRelative
            )
        );

        // Toggle target heading
        m_driverController.square().onTrue(
            m_swerve.commandSetTargetHeadingEnabled(() -> !m_swerve.isTargetingHeading(), Util::targetHeadingTest)
        );

        // m_swerve.setDefaultCommand(
        // m_swerve.driveWithJoystickCommand(
        // () -> m_driverController.getRawAxis(0),
        // () -> m_driverController.getRawAxis(1),
        // () -> m_driverController.getRawAxis(2),
        // () -> Constants.TeleopConstants.kDriveFieldRelative
        // )
        // );
    }

    private void registerNamedCommands() {
        NamedCommands.registerCommand("PrintHello", Commands.print("HELLO WORLD"));

        NamedCommands.registerCommand(
            "Aim",
            Commands.sequence(
                m_swerve.commandSetTargetHeadingEnabled(() -> true, Util::targetHeadingTest),
                Commands.print("AIMING")
            )
        );

        NamedCommands.registerCommand(
            "Shoot",
            Commands.sequence(
                Commands.print("WAITING UNTIL AIM GOOD"),
                Commands.waitUntil(() -> m_swerve.isAtTargetHeading()),
                m_swerve.commandSetTargetHeadingEnabled(() -> false),
                Commands.print("SHOOT")
            )
        );
    }
}
