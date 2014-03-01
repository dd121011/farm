//
//  MoreAroundFarm.h
//  OurFarm
//
//  周边农家院
//
//  Created by 李 凤勇 on 13-7-1.
//  Copyright (c) 2013年 tian hao. All rights reserved.
//

#import "MoreAroundFarm.h"
#import "Summary.h"

#import "AFAppDotNetAPIClient.h"
#import "AFJSONRequestOperation.h"
#import "AFHTTPClient.h"

@implementation MoreAroundFarm

- (id)initWithAttributes:(NSDictionary *)attributes {
    self = [super init];
    if (!self) {
        return nil;
    }
       
    //contanst
    _view_id = [attributes valueForKeyPath:@"view_id"];
    _farmhome_id = [attributes valueForKeyPath:@"farmhome_id"];
    _type = [attributes objectForKey:@"type"];
    _distance = [attributes objectForKey:@"distance"];
    
    _summary = [[Summary alloc] initWithAttributes:[attributes valueForKeyPath:@"summary"]];
    
    return self;
}

//获取周边农家院
+ (void)getMoreAroundListWithBlock:(void (^)(NSArray *moreAroundFarmArray, NSError *error))block
                  requestParameter:param{
    [AFJSONRequestOperation addAcceptableContentTypes:[NSSet setWithObject:@"text/html"]];
    
    [[AFAppDotNetAPIClient sharedClient] postPath:@"web/GetFarmhome.php" parameters:param success:^(AFHTTPRequestOperation *operation, id JSON) {
        
        NSLog(@"JSON: %@", JSON);
        
        NSMutableArray *mutablePosts = [[NSMutableArray alloc] init];
        
        for (NSDictionary *attributes in JSON) {
            MoreAroundFarm *moreAroundFarm = [[MoreAroundFarm alloc] initWithAttributes:attributes];
            [mutablePosts addObject:moreAroundFarm];
        }
        if (block) {
            block([NSArray arrayWithArray:mutablePosts], nil);
        }
    } failure:^(AFHTTPRequestOperation *operation, NSError *error) {
        NSLog(@"Get Around Farm Home Fail: %@", operation.responseString);
        
        if (block) {
            block([NSArray array], error);
        }
    }];
}

@end
