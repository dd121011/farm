//
//  DestinationAnnotation.h
//
//  自定义地图标记显示
//
//  Created by lifengyong on 13-6-18.
//  Copyright 2013年 FarmHome. All rights reserved.
//

#import "DestinationAnnotation.h"

@implementation DestinationAnnotation

@synthesize coordinate,title,subtitle,placeMark,tag,lat,lng;

- (id) initWithCoords:(CLLocationCoordinate2D) coords{   
    self = [super init];   
    if (self != nil) {
        coordinate = coords;    
    }
    return self;   
}

@end
