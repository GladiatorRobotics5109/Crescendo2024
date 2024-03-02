package frc.robot.vision;

import java.util.*;
import java.io.IOException;

// import frc.robot.subsystems.logging.Logger;
import org.photonvision.EstimatedRobotPose;
import org.photonvision.PhotonCamera;
import org.photonvision.PhotonPoseEstimator;
import org.photonvision.PhotonPoseEstimator.PoseStrategy;


import edu.wpi.first.apriltag.AprilTagFieldLayout;
import edu.wpi.first.math.geometry.Transform3d;
import frc.robot.util.Constants;

public class VisionManager {

    private final List<PhotonCamera> m_cameras = new ArrayList<>();
    private final List<PhotonPoseEstimator> m_estimators = new ArrayList<>();

    private AprilTagFieldLayout m_aprilTagFieldLayout;

    public VisionManager(Map<String, Transform3d> visionSources) {
        try {
            m_aprilTagFieldLayout = AprilTagFieldLayout.loadFromResource(Constants.VisionConstants.kApriltagLayout.m_resourceFile);
        } catch (IOException e) {
            
        }


        for (Map.Entry<String, Transform3d> source : visionSources.entrySet()) {
            PhotonCamera camera = new PhotonCamera(source.getKey());

            PhotonPoseEstimator estimator = new PhotonPoseEstimator(
                m_aprilTagFieldLayout,
                PoseStrategy.MULTI_TAG_PNP_ON_COPROCESSOR,
                camera,
                Constants.VisionConstants.kCameraPos
            );

            m_cameras.add(camera);
            m_estimators.add(estimator);
        }
    }

    public Optional<EstimatedRobotPoses> getPose() {
        boolean isAllEmpty = true;
        EstimatedRobotPose[] poses = new EstimatedRobotPose[m_estimators.size()];

        for (int i = 0; i < m_estimators.size(); i++) {
            Optional<EstimatedRobotPose> pose = m_estimators.get(i).update();

            if (pose.isPresent()) {
                isAllEmpty = false;
                poses[i] = pose.get();
            }
            else
            {
                poses[i] = null;
            }
        }

        if (isAllEmpty)
            return Optional.empty();

        EstimatedRobotPoses pose = new EstimatedRobotPoses(poses);
        return Optional.of(pose);
    }
}
