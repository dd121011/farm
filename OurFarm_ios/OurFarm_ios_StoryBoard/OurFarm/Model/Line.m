//
//  Line.m
//  OurFarm
//
//  Created by tian hao on 13-6-21.
//  Copyright (c) 2013年 tian hao. All rights reserved.
//

#import "Line.h"

@implementation Line

-(id)initWithAttributes:(NSDictionary *)attributes {
    self = [super init];
    if (!self) {
        return nil;
    }
     _itineraryId = [attributes valueForKeyPath:@"itineraryId"];
    _stepNum = [attributes valueForKeyPath:@"stepNum"];
    _destinationId = [attributes valueForKeyPath:@"destinationId"];
    _destinationName = [attributes valueForKeyPath:@"destinationName"];
    _content = [attributes valueForKeyPath:@"content"];
    _characteristic = [attributes valueForKeyPath:@"characteristic"];
    _pics = [attributes valueForKeyPath:@"pics"];
    
    return self;
}


@end
