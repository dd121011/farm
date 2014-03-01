//
//  ClassicLine.m
//  OurFarm
//
//  Created by tian hao on 13-6-21.
//  Copyright (c) 2013年 tian hao. All rights reserved.
//

#import "ClassicLine.h"
#import "AFAppDotNetAPIClient.h"
#import "AFJSONRequestOperation.h"
#import "Line.h"

@implementation ClassicLine{
    
}

- (id)initWithAttributes:(NSDictionary *)attributes {
    self = [super init];
    if (!self) {
        return nil;
    }
    
    //contanst
    _itineraryId = [attributes valueForKeyPath:@"itineraryId"];
    _name = [attributes valueForKeyPath:@"name"];
    _itinerarySummary = [attributes objectForKey:@"itinerarySummary"];
    _score = [attributes objectForKey:@"score"];
    _hot = [attributes objectForKey:@"hot"];
    _pic = [attributes objectForKey:@"pic"];
    _price = [attributes objectForKey:@"price"];
    _priceInfo = [attributes objectForKey:@"priceInfo"];
    _characteristic = [attributes objectForKey:@"characteristic"];
    _picMap = [attributes valueForKeyPath:@"picMap"];
    _lines = [attributes valueForKeyPath:@"line"];
    
    NSMutableArray *mutableLines = [[NSMutableArray alloc] init];
    
    for (NSDictionary *oline in _lines) {
        Line *line = [[Line alloc] initWithAttributes:oline];
        [mutableLines addObject:line];
    }
    _lines = mutableLines;
    
    return self;

}

- (NSURL *)avatarImageURL {
    return [NSURL URLWithString:_picMap];
}
    

#pragma mark - get classicLine概要信息

+ (void)getClassicLineSummaryWithBlock:(void (^)(NSArray *posts, NSError *error))block
requestParameter:param {
    
    [AFJSONRequestOperation addAcceptableContentTypes:[NSSet setWithObject:@"text/html"]];
    
    [[AFAppDotNetAPIClient sharedClient] postPath:@"web/ClassicItineraries.php" parameters:param success:^(AFHTTPRequestOperation *operation, id JSON) {
        
        NSLog(@"JSON: %@", JSON);
        
        NSMutableArray *mutablePosts = [[NSMutableArray alloc] init];
        
        for (NSDictionary *attributes in JSON) {
            ClassicLine *summary = [[ClassicLine alloc] initWithAttributes:attributes];
            [mutablePosts addObject:summary];
        }
        if (block) {
            block([NSArray arrayWithArray:mutablePosts], nil);
        }
    } failure:^(AFHTTPRequestOperation *operation, NSError *error) {
        if (block) {
            block([NSArray array], error);
        }
    }];
    
}

@end
