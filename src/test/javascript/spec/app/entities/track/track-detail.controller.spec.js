'use strict';

describe('Controller Tests', function() {

    describe('Track Management Detail Controller', function() {
        var $scope, $rootScope;
        var MockEntity, MockTrack, MockUser, MockStyle;
        var createController;

        beforeEach(inject(function($injector) {
            $rootScope = $injector.get('$rootScope');
            $scope = $rootScope.$new();
            MockEntity = jasmine.createSpy('MockEntity');
            MockTrack = jasmine.createSpy('MockTrack');
            MockUser = jasmine.createSpy('MockUser');
            MockStyle = jasmine.createSpy('MockStyle');
            

            var locals = {
                '$scope': $scope,
                '$rootScope': $rootScope,
                'entity': MockEntity ,
                'Track': MockTrack,
                'User': MockUser,
                'Style': MockStyle
            };
            createController = function() {
                $injector.get('$controller')("TrackDetailController", locals);
            };
        }));


        describe('Root Scope Listening', function() {
            it('Unregisters root scope listener upon scope destruction', function() {
                var eventType = 'soundxtream3App:trackUpdate';

                createController();
                expect($rootScope.$$listenerCount[eventType]).toEqual(1);

                $scope.$destroy();
                expect($rootScope.$$listenerCount[eventType]).toBeUndefined();
            });
        });
    });

});
