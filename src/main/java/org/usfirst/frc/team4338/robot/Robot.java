package org.usfirst.frc.team4338.robot;

import org.usfirst.frc.team4338.robot.vision.Camera;
import org.usfirst.frc.team4338.robot.vision.TapeTarget;

import edu.wpi.first.wpilibj.AnalogGyro;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.Servo;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.Victor;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Robot extends IterativeRobot {
	private static final long PERIODIC_DELAY = 5;

	private double angle;
	private DoubleSolenoid ballFlicker;
	private Camera camera;
	private Controller controller;
	private RobotDrive drive;
	private AnalogGyro gyro;
	private double kp = 0.03; // What's this?
	private Servo leftGearShiftServo;
	private Joystick leftJoystick;
	private Servo rightGearShiftServo;
	private Joystick rightJoystick;
	private Victor shooterAngleMotor;
	private Victor shooterBelt1;
	private Victor shooterBelt2;
	private TapeTarget target;

	/**
	 * The robot for the competition.
	 */
	public Robot() {
		super();

		// Set up vision
		camera = new Camera(Camera.DEFAULT_VIEW_ANGLE);

		// Set up controls
		leftJoystick = new Joystick(0);
		rightJoystick = new Joystick(1);
		controller = new Controller(2);

		// Set up motors
		drive = new RobotDrive(0, 1);
		drive.setExpiration(.1);
		shooterBelt1 = new Victor(4);
		shooterBelt2 = new Victor(5);
		leftGearShiftServo = new Servo(2);
		rightGearShiftServo = new Servo(3);
		shooterAngleMotor = new Victor(6);

		// Set up solenoid
		ballFlicker = new DoubleSolenoid(7, 8);
		ballFlicker.set(DoubleSolenoid.Value.kReverse);

		gyro = new AnalogGyro(0);
	}

	/**
	 * Initialization code for autonomous mode. This method is called each time
	 * the robot enters autonomous mode.
	 */
	@Override
	public void autonomousInit() {
		// TODO
		gyro.reset();
	}

	/**
	 * Periodic code for autonomous mode. This method is called periodically at
	 * a regular rate while the robot is in autonomous mode.
	 */
	@Override
	public void autonomousPeriodic() {
		// TODO
	}

	/**
	 * Initialization code for disabled mode. This method is called each time
	 * the robot enters disabled mode.
	 */
	@Override
	public void disabledInit() {
		// TODO
	}

	/**
	 * Periodic code for disabled mode. This method is called periodically at a
	 * regular rate while the robot is in disabled mode.
	 */
	@Override
	public void disabledPeriodic() {
		// TODO
	}

	/**
	 * Initialization code for the first boot of the robot. This method is
	 * called when the robot is turned on.
	 */
	@Override
	public void robotInit() {
		// TODO
	}

	/**
	 * Shifts the tank drive into a low (fast) gear or a high (slow) gear with a
	 * 0 or 1 toggle respectively.
	 * 
	 * @param state
	 */
	private void shiftGear(int state) {
		if (state == 0) {
			leftGearShiftServo.setAngle(110);
			rightGearShiftServo.setAngle(110);
		} else if (state == 1) {
			leftGearShiftServo.setAngle(50);
			rightGearShiftServo.setAngle(50);
		}
	}

	/**
	 * Initialization code for teleop mode. This method is called each time the
	 * robot enters teleop mode.
	 */
	@Override
	public void teleopInit() {
		// TODO
		gyro.reset();
	}

	/**
	 * Periodic code for teleop mode. This method is called periodically at a
	 * regular rate while the robot is in teleop mode.
	 */
	@Override
	public void teleopPeriodic() {
		camera.captureImage();

		SmartDashboard.putBoolean("Target visible", target.isVisible(camera));

		angle = gyro.getAngle();

		// Shift to high gear if either joystick trigger is pulled
		boolean triggersDown = leftJoystick.getTrigger() || rightJoystick.getTrigger();
		shiftGear(triggersDown ? 1 : 0);

		drive.tankDrive(leftJoystick.getY(), rightJoystick.getY());

		Timer.delay((double) PERIODIC_DELAY / 1000);

		if (controller.getButtonA()) {
			// Creep / Angle
			shiftGear(1);
			// drive.tankDrive(VAL, VAL);

			// Toggle shooter angle
			// If the color of the floor changes
			drive.tankDrive(0, 0);

			// Spin up belts
			shooterBelt1.set(1);
			shooterBelt2.set(1);
			Timer.delay(2);

			// Shoot
			ballFlicker.set(DoubleSolenoid.Value.kForward);
			Timer.delay(1);
			ballFlicker.set(DoubleSolenoid.Value.kReverse);
			shooterBelt1.set(0);
			shooterBelt2.set(0);
		}
	}

	/**
	 * Initialization code for test mode. This method is called each time the
	 * robot enters test mode.
	 */
	@Override
	public void testInit() {
		// TODO
	}

	/**
	 * Periodic code for test mode. This method is called periodically at a
	 * regular rate while the robot is in test mode.
	 */
	@Override
	public void testPeriodic() {
		// TODO
	}

}
