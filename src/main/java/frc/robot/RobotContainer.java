// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import static edu.wpi.first.units.Units.*;

import com.ctre.phoenix6.swerve.SwerveModule.DriveRequestType;
import com.ctre.phoenix6.swerve.SwerveRequest;
import com.pathplanner.lib.commands.PathPlannerAuto;

import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
import edu.wpi.first.wpilibj2.command.sysid.SysIdRoutine.Direction;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import frc.robot.subsystems.CommandSwerveDrivetrain;

import frc.robot.generated.TunerConstants;
import frc.robot.subsystems.CommandSwerveDrivetrain;

public class RobotContainer {
    private double MaxSpeed = TunerConstants.kSpeedAt12Volts.in(MetersPerSecond); // kSpeedAt12Volts desired top speed
    private double MaxAngularRate = RotationsPerSecond.of(0.75).in(RadiansPerSecond); // 3/4 of a rotation per second max angular velocity

    /* Setting up bindings for necessary control of the swerve drive platform */
    private final SwerveRequest.FieldCentric drive = new SwerveRequest.FieldCentric()
            .withDeadband(MaxSpeed * 0.2).withRotationalDeadband(MaxAngularRate * 0.2) // Add a 10% deadband
            .withDriveRequestType(DriveRequestType.OpenLoopVoltage); // Use open-loop control for drive motors
    private final SwerveRequest.SwerveDriveBrake brake = new SwerveRequest.SwerveDriveBrake();
    private final SwerveRequest.PointWheelsAt point = new SwerveRequest.PointWheelsAt();

    private final Telemetry logger = new Telemetry(MaxSpeed);

    private final CommandXboxController joystick = new CommandXboxController(0);

    public final CommandSwerveDrivetrain drivetrain = TunerConstants.createDrivetrain();

    private ShuffleboardTab tab = Shuffleboard.getTab("Tab1");


    private final SendableChooser<Command> chooser = new SendableChooser<>();

    public RobotContainer() {
        configureBindings();
        
        // Autos
        chooser.addOption("NinetyDegree", degreeTest()
        // .finallyDo(() -> {drivetrain.zeroHeading();})
        );

        chooser.addOption("OneMeter", meterTest());

        chooser.addOption("90degree", Test90degree());

        chooser.addOption("Fun", funTest() );

        chooser.addOption("Circle", circle());

        chooser.addOption("rotatingCircle", rotatingCircle());

        chooser.addOption("rotatingCircle2", rotatingCircle2());

        chooser.addOption("Diamond", Diamond());

        chooser.addOption("DiamondChoreo", DiamondChoreo());

        chooser.addOption("OneMeterChoreo", OneMeterChoreo());

        chooser.addOption("PlusChoreo", PlusChoreo());

        chooser.addOption("StackedWavesChoreo", StackedWavesChoreo());

        chooser.addOption("Coral5PreloadedMidChoreo", Coral5PreloadedMidChoreo());

        SmartDashboard.putData("Auto choices", chooser);
        tab.add("Auto Chooser", chooser);
    }

    private void configureBindings() {
        // Note that X is defined as forward according to WPILib convention,
        // and Y is defined as to the left according to WPILib convention.
        drivetrain.setDefaultCommand(
            // Drivetrain will execute this command periodically
            drivetrain.applyRequest(() ->
                drive.withVelocityX(-joystick.getLeftY() * MaxSpeed) // Drive forward with negative Y (forward)
                    .withVelocityY(-joystick.getLeftX() * MaxSpeed) // Drive left with negative X (left)
                    .withRotationalRate(-joystick.getRightX() * MaxAngularRate) // Drive counterclockwise with negative X (left)
            )
        );

        joystick.a().whileTrue(drivetrain.applyRequest(() -> brake));
        joystick.b().whileTrue(drivetrain.applyRequest(() ->
            point.withModuleDirection(new Rotation2d(-joystick.getLeftY(), -joystick.getLeftX()))
        ));

        // Run SysId routines when holding back/start and X/Y.
        // Note that each routine should be run exactly once in a single log.
        joystick.back().and(joystick.y()).whileTrue(drivetrain.sysIdDynamic(Direction.kForward));
        joystick.back().and(joystick.x()).whileTrue(drivetrain.sysIdDynamic(Direction.kReverse));
        joystick.start().and(joystick.y()).whileTrue(drivetrain.sysIdQuasistatic(Direction.kForward));
        joystick.start().and(joystick.x()).whileTrue(drivetrain.sysIdQuasistatic(Direction.kReverse));

        // reset the field-centric heading on left bumper press
        joystick.leftBumper().onTrue(drivetrain.runOnce(() -> drivetrain.seedFieldCentric()));

        drivetrain.registerTelemetry(logger::telemeterize);
    }

    public Command getAutonomousCommand() {
        return chooser.getSelected();
    }
    
    // Auto Commands
    public Command degreeTest() {
        return new PathPlannerAuto("NinetyDegree");
    }
    public Command meterTest() {
        return new PathPlannerAuto("OneMeter");
    }
    public Command Test90degree() {
        return new PathPlannerAuto("90degree");
    }
    public Command funTest() {
        return new PathPlannerAuto("Fun");
    }
    public Command circle() {
        return new PathPlannerAuto("Circle");
    }
    public Command rotatingCircle() {
        return new PathPlannerAuto("rotatingCircle");
    }
    public Command rotatingCircle2() {
        return new PathPlannerAuto("rotatingCircle2");
    }
    public Command Diamond() {
        return new PathPlannerAuto("Diamond");
    }
    public Command DiamondChoreo() {
        return new PathPlannerAuto("DiamondChoreo");
    }
    public Command OneMeterChoreo() {
        return new PathPlannerAuto("OneMeterChoreo");
    }
    public Command PlusChoreo() {
        return new PathPlannerAuto("PlusChoreo");
    }
    public Command StackedWavesChoreo() {
        return new PathPlannerAuto("StackedWavesChoreo");
    }
    public Command Coral5PreloadedMidChoreo() {
        return new PathPlannerAuto("Coral5PreloadedMidChoreo");
    }
}
