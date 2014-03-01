//
//  DestinationAnnotation.h
//
//  自定义地图标记显示
//
//  Created by lifengyong on 13-6-18.
//  Copyright 2013年 FarmHome. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <CoreLocation/CoreLocation.h>
#import <MapKit/MapKit.h>

@interface DestinationAnnotation : MKPinAnnotationView<MKAnnotation>
{
    CLLocationCoordinate2D location;  
    CLPlacemark *placeMark;
    NSString *title;  
    NSString *subtitle;  
    NSNumber *tag;
}
@property (nonatomic,readonly) CLLocationCoordinate2D coordinate;   
@property (nonatomic,retain) CLPlacemark *placeMark;   
@property (nonatomic,copy) NSString *subtitle;   
@property (nonatomic,copy) NSString *title;   
@property (nonatomic) NSNumber *tag;
@property (nonatomic) double lat;
@property (nonatomic) double lng;

- (id) initWithCoords:(CLLocationCoordinate2D) coords;
@end
