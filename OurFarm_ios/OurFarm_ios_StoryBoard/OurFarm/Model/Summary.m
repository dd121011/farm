//
//  Summary.m
//  OurFarm_ios
//
//  景点概要信息
//
//  Created by 李 凤勇 on 13-5-30.
//  Copyright (c) 2013年 FarmHome. All rights reserved.
//

#import "Summary.h"
#import "AFAppDotNetAPIClient.h"
#import "AFJSONRequestOperation.h"
#import "SBJson.h"
#import "SBJsonParser.h"
#import "AFHTTPClient.h"

@implementation Summary {
@private
    NSString *_avatarImageURLString;
}


- (id)initWithAttributes:(NSDictionary *)attributes {
    self = [super init];
    if (!self) {
        return nil;
    }
    
    //contanst
    _destinationId = [attributes valueForKeyPath:@"destinationId"];
    _name = [attributes valueForKeyPath:@"name"];
    _address = [attributes objectForKey:@"address"];
    _priceInfo = [attributes objectForKey:@"priceInfo"];
    _distance = [attributes objectForKey:@"distance"];
    _characteristic = [attributes objectForKey:@"characteristic"];
    _score = [attributes objectForKey:@"score"];
    
    _avatarImageURLString = [attributes valueForKeyPath:@"pic"];
    _pic = [attributes valueForKeyPath:@"pic"];
    _lat = [attributes valueForKeyPath:@"lat"];
    _lng = [attributes valueForKeyPath:@"lng"];
    
    return self;
}

- (NSURL *)avatarImageURL {
    return [NSURL URLWithString:_avatarImageURLString];
}

#pragma mark - get search概要信息

+ (void)getSearchSummaryWithBlock:(void (^)(NSArray *posts, NSError *error))block
                 requestParameter:param {
    
    [AFJSONRequestOperation addAcceptableContentTypes:[NSSet setWithObject:@"text/html"]];
    
    [[AFAppDotNetAPIClient sharedClient] postPath:@"web/Search.php" parameters:param success:^(AFHTTPRequestOperation *operation, id JSON) {
        
        NSLog(@"JSON: %@", JSON);
        
        NSMutableArray *mutablePosts = [[NSMutableArray alloc] init];
        
        for (NSDictionary *attributes in JSON) {
            Summary *summary = [[Summary alloc] initWithAttributes:attributes];
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

#pragma mark - get recommend概要信息

+ (void)getRecommendSummaryWithBlock:(void (^)(NSArray *posts, NSError *error))block
                    requestParameter:param {
    
    [AFJSONRequestOperation addAcceptableContentTypes:[NSSet setWithObject:@"text/html"]];
    
    [[AFAppDotNetAPIClient sharedClient] postPath:@"web/Recommend.php" parameters:param success:^(AFHTTPRequestOperation *operation, id JSON) {
        
        NSLog(@"JSON: %@", JSON);
        
        NSMutableArray *mutablePosts = [[NSMutableArray alloc] init];
        
        for (NSDictionary *attributes in JSON) {
            Summary *summary = [[Summary alloc] initWithAttributes:attributes];
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

#pragma mark - get nearby概要信息

+ (void)getNearbySummaryWithBlock:(void (^)(NSArray *posts, NSError *error))block
                 requestParameter:param {
    
    [AFJSONRequestOperation addAcceptableContentTypes:[NSSet setWithObject:@"text/html"]];
  
    [[AFAppDotNetAPIClient sharedClient] postPath:@"web/Nearby.php" parameters:param success:^(AFHTTPRequestOperation *operation, id JSON) {

        NSLog(@"JSON: %@", JSON);
                
        NSMutableArray *mutablePosts = [[NSMutableArray alloc] init];
        
        for (NSDictionary *attributes in JSON) {
            Summary *summary = [[Summary alloc] initWithAttributes:attributes];
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

#pragma mark - get Plans概要信息

+ (void)getPlansSummaryWithBlock:(void (^)(NSArray *posts, NSError *error))block
                 requestParameter:param {
    
    [AFJSONRequestOperation addAcceptableContentTypes:[NSSet setWithObject:@"text/html"]];
    
    [[AFAppDotNetAPIClient sharedClient] postPath:@"web/Plans.php" parameters:param success:^(AFHTTPRequestOperation *operation, id JSON) {
        
        NSLog(@"JSON: %@", JSON);
        
        NSMutableArray *mutablePosts = [[NSMutableArray alloc] init];
        
        for (NSDictionary *attributes in JSON) {
            Summary *summary = [[Summary alloc] initWithAttributes:attributes];
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

#pragma mark - get hot概要信息

+ (void)getHotTopSummaryWithBlock:(void (^)(NSArray *posts, NSError *error))block
                requestParameter:param {
    
    [AFJSONRequestOperation addAcceptableContentTypes:[NSSet setWithObject:@"text/html"]];
    
    [[AFAppDotNetAPIClient sharedClient] postPath:@"web/HotTop.php" parameters:param success:^(AFHTTPRequestOperation *operation, id JSON) {
        
        NSLog(@"JSON: %@", JSON);
        
        NSMutableArray *mutablePosts = [[NSMutableArray alloc] init];
        
        for (NSDictionary *attributes in JSON) {
            Summary *summary = [[Summary alloc] initWithAttributes:attributes];
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

#pragma mark - get AccurateFind概要信息

+ (void)getAccurateFindSummaryWithBlock:(void (^)(NSArray *posts, NSError *error))block
                 requestParameter:param {
    
    [AFJSONRequestOperation addAcceptableContentTypes:[NSSet setWithObject:@"text/html"]];
    
    [[AFAppDotNetAPIClient sharedClient] postPath:@"web/AccurateFind.php" parameters:param success:^(AFHTTPRequestOperation *operation, id JSON) {
        
        NSLog(@"JSON: %@", JSON);
        
        NSMutableArray *mutablePosts = [[NSMutableArray alloc] init];
        
        for (NSDictionary *attributes in JSON) {
            Summary *summary = [[Summary alloc] initWithAttributes:attributes];
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
