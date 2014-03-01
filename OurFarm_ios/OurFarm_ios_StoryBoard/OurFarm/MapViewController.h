//
//  MapViewController.h
//  OurFarm
//
//  Created by tian hao on 13-6-18.
//  Copyright (c) 2013年 tian hao. All rights reserved.
//

#import <UIKit/UIKit.h>
#import <MapKit/MapKit.h>
#import "DestinationAnnotation.h"
#import "Summary.h"
#import "DetailViewController.h"




@interface MapViewController : UIViewController<CLLocationManagerDelegate,MKMapViewDelegate>
{
    MKMapView *mapView;
    CLLocationManager *locationManager;
    CLLocationCoordinate2D coordinate;
    CLGeocoder *geoCoder;
}

@property (strong, nonatomic) IBOutlet MKMapView *mapView;
@property NSMutableArray *summaryArray;

- (void)locationAddressWithLocation:(CLLocation *)locationGps
                        withSummary:(Summary *) summary;

@end
