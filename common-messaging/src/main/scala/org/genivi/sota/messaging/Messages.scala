package org.genivi.sota.messaging

import java.time.Instant
import java.util.UUID

import akka.http.scaladsl.model.Uri
import cats.syntax.show._
import com.advancedtelematic.libats.messaging.Messages.MessageLike
import io.circe.{Decoder, Encoder}
import org.genivi.sota.data.DeviceStatus.DeviceStatus
import org.genivi.sota.marshalling.CirceInstances._
import org.genivi.sota.data._
import org.genivi.sota.data.UpdateType.UpdateType

object Messages {

  import Device._

  val partitionPrefixSize = 256

  final case class UriWithSimpleEncoding(uri: Uri)

  object UriWithSimpleEncoding {
    implicit val uriWithSimpleEncoding: Encoder[UriWithSimpleEncoding] =
      Encoder[String].contramap(_.uri.toString)
    implicit val uriWithSimpleDecoding: Decoder[UriWithSimpleEncoding] =
      Decoder[String].map(Uri.apply).map(UriWithSimpleEncoding.apply)
  }

  final case class DeviceSeen(
    namespace: Namespace,
    uuid: Uuid,
    lastSeen: Instant)

  final case class DeviceCreated(
    namespace: Namespace,
    uuid: Uuid,
    deviceName: DeviceName,
    deviceId: Option[DeviceId],
    deviceType: DeviceType,
    timestamp: Instant = Instant.now())

  final case class DevicePublicCredentialsSet(
    namespace: Namespace,
    uuid: Uuid,
    credentials: String,
    timestamp: Instant = Instant.now())

  final case class DeviceActivated(
    namespace: Namespace,
    uuid: Uuid,
    at: Instant)

  final case class DeviceDeleted(
    namespace: Namespace,
    uuid: Uuid,
    timestamp: Instant = Instant.now())

  final case class PackageCreated(
    namespace: Namespace,
    packageId: PackageId,
    description: Option[String],
    vendor: Option[String],
    signature: Option[String],
    timestamp: Instant = Instant.now())

  final case class TreehubCommit (
    ns: Namespace,
    commit: String,
    refName: String,
    description: String,
    size: Int,
    uri: String)

  final case class PackageBlacklisted(
    namespace: Namespace,
    packageId: PackageId,
    timestamp: Instant = Instant.now())

  final case class ImageStorageUsage(namespace: Namespace, timestamp: Instant, byteCount: Long)

  final case class PackageStorageUsage(namespace: Namespace, timestamp: Instant, byteCount: Long)

  final case class BandwidthUsage(id: UUID, namespace: Namespace, timestamp: Instant, byteCount: Long,
                                  updateType: UpdateType, updateId: String)

  //Create custom UpdateSpec here instead of using org.genivi.sota.core.data.UpdateSpec as that would require moving
  //multiple RVI messages into SotaCommon. Furthermore, for now this class contains just the info required by the
  //front end.
  final case class UpdateSpec(
    namespace: Namespace,
    device: Uuid,
    packageUuid: UUID,
    status: String,
    timestamp: Instant = Instant.now())

  final case class UserCreated(id: String)

  final case class UserLogin(id: String, timestamp: Instant)

  final case class DeviceUpdateStatus(namespace: Namespace,
                                      device: Uuid,
                                      status: DeviceStatus,
                                      timestamp: Instant = Instant.now())

  final case class CampaignLaunched(namespace: Namespace, updateId: Uuid, devices: Set[Uuid],
                                    pkgUri: UriWithSimpleEncoding, pkg: PackageId,
                                    pkgSize: Long, pkgChecksum: String)

  implicit val deviceSeenMessageLike = MessageLike[DeviceSeen](_.uuid.show)

  implicit val deviceCreatedMessageLike = MessageLike[DeviceCreated](_.uuid.show)

  implicit val devicePublicCredentialsSetMessageLike = MessageLike[DevicePublicCredentialsSet](_.uuid.show)

  implicit val deviceDeletedMessageLike = MessageLike[DeviceDeleted](_.uuid.show)

  implicit val deviceActivatedMessageLike = MessageLike[DeviceActivated](_.uuid.show)

  implicit val packageCreatedMessageLike = MessageLike[PackageCreated](_.packageId.mkString)

  implicit val treehubCommitMessageLike = MessageLike[TreehubCommit](_.commit)

  implicit val updateSpecMessageLike = MessageLike[UpdateSpec](_.device.show)

  implicit val blacklistedPackageMessageLike = MessageLike[PackageBlacklisted](_.packageId.mkString)

  implicit val imageStorageUsageMessageLike = MessageLike[ImageStorageUsage](_.namespace.get)

  implicit val packageStorageUsageMessageLike = MessageLike[PackageStorageUsage](_.namespace.get)

  implicit val bandwidthUsageMessageLike = MessageLike[BandwidthUsage](_.id.toString)

  implicit val userCreatedMessageLike = MessageLike[UserCreated](_.id)

  implicit val userLoginMessageLike = MessageLike[UserLogin](_.id)

  implicit val deviceStatusMessageLike = MessageLike[DeviceUpdateStatus](_.device.show)

  implicit val campaignLaunchedMessageLike = MessageLike[CampaignLaunched](_.updateId.show)
}
