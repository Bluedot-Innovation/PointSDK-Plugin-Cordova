
@class BDGeometry;

/**
  @brief Objects having an inherent @ref BDGeometry "geometry".
*/
@protocol BDPSpatialObject <NSObject>

/**
  @returns The geometry inherent to this object.
 */
- (BDGeometry *)geometry;

@end

