//
//  Line.h
//  OurFarm
//
//  Created by tian hao on 13-6-21.
//  Copyright (c) 2013年 tian hao. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface Line : NSObject

@property NSNumber *itineraryId;
@property NSNumber *stepNum;
@property NSNumber *destinationId;
@property NSString *destinationName;
@property NSString *content;
@property (nonatomic, copy) NSMutableArray *pics ;//图片URL
@property (nonatomic, copy) NSString *characteristic;//特色，用$分隔


-(id)initWithAttributes:(NSDictionary *)attributes;

@end
